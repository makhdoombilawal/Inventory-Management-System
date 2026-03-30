import requests
import json
import os
import datetime
import uuid

BASE_URL = "http://localhost:8080/api"
QA_DIR = "qa-response"

if not os.path.exists(QA_DIR):
    os.makedirs(QA_DIR)

summary = []

def log_summary(test_name, method, endpoint, status, expected_status, response_body=None):
    result = "PASS" if status == expected_status or (expected_status == 204 and status == 200) else f"FAIL (Expected {expected_status}, got {status})"
    
    timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S_%f")
    filename = f"{test_name}_{timestamp}.json"
    
    with open(os.path.join(QA_DIR, filename), "w") as f:
        data = {
            "testCase": test_name,
            "status": f"{method} {endpoint}",
            "result": result,
            "httpStatus": status,
            "response": response_body if response_body else {}
        }
        json.dump(data, f, indent=2)

    summary.append({
        "test": test_name,
        "request": f"{method} {endpoint}",
        "status": status,
        "result": result
    })
    print(f"[{result}] {test_name} - {method} {endpoint} ({status})")

def test_api(method, endpoint, body=None, token=None, test_name="", expected_status=200):
    """
    Strict JWT-only test helper.
    ENTERPRISE RULE: NO X-Tenant-Id header is ever sent.
    Tenant context is always derived from the JWT Bearer token.
    """
    url = BASE_URL + endpoint
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
        
    try:
        if method == "GET":
            resp = requests.get(url, headers=headers)
        elif method == "POST":
            resp = requests.post(url, json=body, headers=headers)
        elif method == "PUT":
            resp = requests.put(url, json=body, headers=headers)
        elif method == "DELETE":
            resp = requests.delete(url, headers=headers)
        
        try:
            resp_json = resp.json()
        except Exception:
            resp_json = {"raw": resp.text}
            
        log_summary(test_name, method, endpoint, resp.status_code, expected_status, resp_json)
        return resp_json
        
    except Exception as e:
        log_summary(test_name, method, endpoint, 500, expected_status, {"error": str(e)})
        return None

def run_tests():
    print("=" * 65)
    print("  ENTERPRISE QA - JWT-Based Multi-Tenant Isolation Audit")
    print("=" * 65)
    
    # =========================================================
    # 1. GLOBAL ADMIN AUTH
    # =========================================================
    ga_resp = test_api("POST", "/auth/login",
        {"email": "admin@system.com", "password": "Admin@123"},
        token=None, test_name="01_Login_Global_Admin", expected_status=200)

    ga_token = ga_resp.get("data", {}).get("token") if "data" in ga_resp else ga_resp.get("token")
    if not ga_token:
        print("FATAL: Global Admin Login failed. Aborting.")
        return

    test_api("GET", "/admin/health", token=ga_token, test_name="02_Admin_HealthCheck", expected_status=200)
    test_api("GET", "/admin/dealers/total", token=ga_token, test_name="03_Admin_TotalDealers", expected_status=200)
    test_api("GET", "/admin/dealers/countBySubscription", token=ga_token, test_name="04_Admin_CountBySubscription", expected_status=200)

    # =========================================================
    # 2. TENANT AUTO-GENERATION & USER REGISTRATION
    # =========================================================
    uid = str(uuid.uuid4())[:8]
    email_a = f"tenant_a_{uid}@test.com"

    # Registration WITHOUT tenantId creates a NEW tenant automatically
    reg_a = test_api("POST", "/users/register",
        {"email": email_a, "password": "Pass@123"},
        test_name="05_AutoTenant_Registration_A", expected_status=201)

    tenant_a_id = reg_a.get("data", {}).get("tenantId") if "data" in reg_a else None
    if not tenant_a_id:
        print("FATAL: Registration failed to auto-generate tenant. Aborting.")
        return
    print(f"  --> Auto-Generated Tenant A ID: {tenant_a_id}")

    # =========================================================
    # 3. TENANT A - LOGIN & VERIFY JWT TENANT RESOLUTION
    # =========================================================
    login_a = test_api("POST", "/auth/login",
        {"email": email_a, "password": "Pass@123"},
        test_name="06_Login_Tenant_A", expected_status=200)
    token_a = login_a.get("data", {}).get("token") if "data" in login_a else None
    if not token_a:
        print("FATAL: Tenant A login failed. Aborting.")
        return

    # User profile should reflect correct JWT tenant
    test_api("GET", "/users/profile", token=token_a, test_name="07_Get_Profile_Tenant_A", expected_status=200)

    # =========================================================
    # 4. DEALER MANAGEMENT (Tenant A)
    # =========================================================
    dealers_resp = test_api("GET", "/dealers", token=token_a, test_name="08_Get_Dealers_Tenant_A", expected_status=200)
    dealer_list = dealers_resp.get("data", []) if isinstance(dealers_resp, dict) else []

    if not dealer_list:
        d_resp = test_api("POST", "/dealers",
            {"name": f"Dealer {uid}", "email": f"contact_{uid}@dealer.com",
             "phoneNumber": "123456789", "subscriptionType": "BASIC", "contactPerson": "Manager"},
            token=token_a, test_name="09_Create_Dealer", expected_status=201)
        dealer_id = d_resp.get("data", {}).get("id") if "data" in d_resp else None
    else:
        dealer_id = dealer_list[0].get("id")
        print(f"  --> Found Auto-Created Dealer ID: {dealer_id}")

    if dealer_id:
        test_api("GET", f"/dealers/{dealer_id}", token=token_a, test_name="10_Get_Dealer_By_Id", expected_status=200)

    # =========================================================
    # 5. VEHICLE CRUD (Tenant A)
    # =========================================================
    v_resp = test_api("POST", "/vehicles",
        {"dealerId": dealer_id, "model": "X5 QA", "manufacturer": "BMW",
         "year": "2024", "price": 85000, "status": "AVAILABLE", "vin": f"VIN-{uid}"},
        token=token_a, test_name="11_Create_Vehicle", expected_status=201)
    vehicle_id = v_resp.get("data", {}).get("id") if "data" in v_resp else None
    
    if vehicle_id:
        test_api("GET", f"/vehicles/{vehicle_id}", token=token_a, test_name="12_Get_Vehicle", expected_status=200)
        test_api("GET", "/vehicles/all", token=token_a, test_name="13_Get_All_Vehicles", expected_status=200)
        test_api("POST", "/vehicles/search", {"model": "X5 QA"}, token=token_a, test_name="14_Search_Vehicles", expected_status=200)

    # =========================================================
    # 6. INVENTORY & ORDERS (Tenant A)
    # =========================================================
    if vehicle_id and dealer_id:
        test_api("PUT", "/inventory/stock",
            {"dealerId": dealer_id, "vehicleId": vehicle_id, "quantity": 10},
            token=token_a, test_name="15_Update_Inventory_Stock", expected_status=200)

        test_api("GET", "/inventory", token=token_a, test_name="16_Get_Inventory", expected_status=200)
        test_api("GET", f"/inventory/dealer/{dealer_id}", token=token_a, test_name="17_Get_Dealer_Inventory", expected_status=200)

        o_resp = test_api("POST", "/orders",
            {"dealerId": dealer_id, "items": [{"vehicleId": vehicle_id, "quantity": 1}]},
            token=token_a, test_name="18_Create_Order", expected_status=201)
        order_id = o_resp.get("data", {}).get("id") if "data" in o_resp else None

        if order_id:
            test_api("GET", "/orders", token=token_a, test_name="19_Get_All_Orders", expected_status=200)
            test_api("GET", f"/orders/{order_id}", token=token_a, test_name="20_Get_Order_By_Id", expected_status=200)
            test_api("PUT", "/orders/status",
                {"id": order_id, "status": "COMPLETED"},
                token=token_a, test_name="21_Update_Order_Status", expected_status=200)

    # =========================================================
    # 7. CROSS-TENANT ISOLATION CHECK
    # =========================================================
    print("\n--- Cross-Tenant Isolation Verification ---")
    uid2 = str(uuid.uuid4())[:8]
    email_b = f"tenant_b_{uid2}@test.com"

    reg_b = test_api("POST", "/users/register",
        {"email": email_b, "password": "Pass@123"},
        test_name="22_AutoTenant_Registration_B", expected_status=201)
    tenant_b_id = reg_b.get("data", {}).get("tenantId") if "data" in reg_b else None
    print(f"  --> Auto-Generated Tenant B ID: {tenant_b_id}")

    login_b = test_api("POST", "/auth/login",
        {"email": email_b, "password": "Pass@123"},
        test_name="23_Login_Tenant_B", expected_status=200)
    token_b = login_b.get("data", {}).get("token") if "data" in login_b else None

    if token_b and vehicle_id:
        # Tenant B tries to access Tenant A's vehicle using their OWN JWT
        # EXPECTED: 403 or 401 Unauthorized (Cross-tenant access denied)
        test_api("GET", f"/vehicles/{vehicle_id}", token=token_b,
            test_name="24_ISO_TenantB_AcccessTenantA_Vehicle", expected_status=403)

    # Verify X-Tenant-Id header is ignored: Send fake tenant header with Tenant A token
    # The server must ignore the header and resolve context from JWT only
    if token_a and vehicle_id:
        url = f"{BASE_URL}/vehicles/{vehicle_id}"
        headers_with_fake_header = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {token_a}",
            "X-Tenant-Id": str(99999)  # Fake tenant - must be IGNORED by server
        }
        try:
            resp = requests.get(url, headers=headers_with_fake_header)
            # Must succeed (200) because JWT overrides the fake header
            result = "PASS" if resp.status_code == 200 else f"FAIL (Expected 200, got {resp.status_code})"
            print(f"[{result}] 25_ISO_FakeTenantHeader_Ignored - GET {url} ({resp.status_code})")
            try:
                resp_json = resp.json()
            except Exception:
                resp_json = {"raw": resp.text}
            log_summary("25_ISO_FakeTenantHeader_Ignored", "GET", f"/vehicles/{vehicle_id}",
                resp.status_code, 200, resp_json)
        except Exception as ex:
            print(f"[ERROR] 25_ISO_FakeTenantHeader_Ignored: {ex}")

    # =========================================================
    # 8. ADMIN CAN CREATE ADMIN USER
    # =========================================================
    admin_uid = str(uuid.uuid4())[:8]
    test_api("POST", "/admin/users",
        {"email": f"newadmin_{admin_uid}@system.com", "password": "Admin@123",
         "role": "ADMIN", "tenantId": tenant_a_id},
        token=ga_token, test_name="26_Admin_Create_Tenant_Admin", expected_status=201)

    # =========================================================
    # FINAL SUMMARY
    # =========================================================
    passed = sum(1 for s in summary if "PASS" in s["result"])
    failed = sum(1 for s in summary if "FAIL" in s["result"])
    
    with open(os.path.join(QA_DIR, "qa_execution_summary.json"), "w") as f:
        json.dump({
            "tests": summary,
            "total": len(summary),
            "passed": passed,
            "failed": failed,
            "timestamp": str(datetime.datetime.now())
        }, f, indent=2)

    print("\n" + "=" * 65)
    print(f"  QA COMPLETE: {passed} PASSED | {failed} FAILED | {len(summary)} TOTAL")
    print(f"  Reports saved to: {QA_DIR}/")
    print("=" * 65)

if __name__ == "__main__":
    run_tests()
