import requests

def test_route():
    print(requests.post("http://127.0.0.1:9090/route", {}).text)

def test_auth():
    print(requests.post("http://127.0.0.1:8080/auth/register", {}).text)

    print(requests.post("http://127.0.0.1:8080/auth/login", {}).text)