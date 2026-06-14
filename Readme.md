# PcShop

PcShop is a full-stack online store application built with Spring Boot and React. The backend exposes REST APIs for products, users, credit cards, and orders. The frontend is a Vite React app that consumes those APIs.

## Tech Stack

- Java 21
- Spring Boot 3.3
- Gradle
- PostgreSQL for normal runtime
- H2 for tests and local demo runs
- React 18
- Vite
- Material UI
- Python seed scripts for loading sample products and users

## Project Structure

```text
.
├── src/main/java/com/academic/PcShop      Spring Boot backend
├── src/main/resources                     Backend configuration and keystore
├── src/main/client/pcShop                 React/Vite frontend
├── src/test                               Spring Boot tests and test config
├── scripts                                Python scripts for database seed data
└── .github/workflows/actions.yml          GitHub Actions build workflow
```

## Prerequisites

- Java 21
- Node.js 22 or compatible
- npm
- Python 3.11 or compatible
- PostgreSQL, unless running the backend with the test profile

## Backend Configuration

The backend reads runtime values from `appProperties.env` in the project root. This file is intentionally ignored by Git.

Create `appProperties.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=password
SSL_KEY_STORE_PASSWORD=pcshop
```

For HTTPS runtime, the backend uses `src/main/resources/keystore.p12`.

## Frontend Configuration

The React app supports a Vite environment variable for the backend API URL.

For local HTTP development, create `src/main/client/pcShop/.env.local`:

```env
VITE_HTTPS=false
VITE_API_BASE_URL=http://localhost:8080/api/
```

Without `VITE_HTTPS=false`, Vite uses the local HTTPS plugin.

## Run Locally

Install frontend dependencies:

```powershell
cd src\main\client\pcShop
npm install
```

Run the backend with the normal PostgreSQL configuration:

```powershell
.\gradlew.bat bootRun
```

For a quick local demo without PostgreSQL, run the backend with the test profile and H2:

```powershell
.\gradlew.bat bootTestRun --args="--spring.profiles.active=test --server.ssl.enabled=false"
```

Run the frontend:

```powershell
cd src\main\client\pcShop
npm run dev
```

Open:

```text
http://127.0.0.1:5173/
```

## Build And Test

Build the frontend:

```powershell
cd src\main\client\pcShop
npm run build
```

Run backend tests and build:

```powershell
.\gradlew.bat clean build
```

The test suite uses `src/test/resources/application-test.properties`, which configures an in-memory H2 database so CI does not require PostgreSQL credentials.

## Seeding The Database

The `scripts` folder contains Python scripts that populate data through the running backend API:

- `scripts/product.py` creates sample products and fetches product images through Google Custom Search.
- `scripts/webUser.py` creates sample users with Faker-generated data.

Install Python dependencies:

```powershell
py -m pip install requests python-dotenv faker
```

Create or update `scripts/.env`:

```env
API_URL_PRODUCT=http://localhost:8080/api/product
API_URL_WEB_USER=http://localhost:8080/api/webUser

# Required only by product.py for image lookup
API_KEY=your_google_custom_search_api_key
CX=your_google_custom_search_engine_id
SEARCH_URL=https://www.googleapis.com/customsearch/v1
```

Start the backend first, then run:

```powershell
cd scripts
py webUser.py
py product.py
```

If the backend is running over HTTPS with a self-signed certificate, the scripts already call the API with certificate verification disabled. For browser-based local development, HTTP is simpler.

## API Overview

The backend serves API routes under `/api`. During local HTTP development, use:

```text
http://localhost:8080/api
```

During HTTPS runtime, use:

```text
https://localhost:8080/api
```

### Users

Base path:

```text
/api/webUser
```

Create a user:

```http
POST /api/webUser
Content-Type: application/json
```

Example request:

```json
{
  "username": "test",
  "password": "test123",
  "firstName": "Test",
  "lastName": "User",
  "phoneNumber": 6900000000,
  "email": "test@example.com",
  "creditCard": [],
  "address": {
    "city": "Athens",
    "street": "Local",
    "number": 1,
    "zipCode": 10000
  },
  "orders": []
}
```

The password is hashed before the user is stored.

Log in:

```http
POST /api/webUser/user?username=test&password=test123
```

Fetch users:

```http
GET /api/webUser?username=test
GET /api/webUser?email=test@example.com
GET /api/webUser?phoneNumber=6900000000
```

Delete a user:

```http
DELETE /api/webUser?username=test
```

Add a credit card to a user:

```http
PUT /api/webUser/add-credit-card?id=1
Content-Type: application/json
```

Example request:

```json
{
  "nameOnCard": "Test User",
  "numberOnCard": "4111111111111111",
  "cardExpireDate": "12/2030",
  "cardType": "VISA"
}
```

Get a user's credit cards:

```http
GET /api/webUser/get-credit-card?id=1
```

### Products

Base path:

```text
/api/product
```

Supported categories:

```text
PC_LAPTOPS
GAMING
MOBILE_TABLETS
IMAGE_SOUND
HARDWARE
PRINTERS
```

Supported availability values:

```text
AVAILABLE
NOT_AVAILABLE
ORDER
PRE_ORDER
```

Create a product:

```http
POST /api/product
Content-Type: application/json
```

Example request:

```json
{
  "uuid": "8a28afbb-2fc1-48dc-b592-7ca21f369e96",
  "productName": "Gaming Mouse Logitech G502",
  "price": 49.99,
  "category": "GAMING",
  "description": "High-precision gaming mouse with customizable buttons.",
  "availability": "AVAILABLE",
  "images": [
    {
      "data": "base64-encoded-image-data"
    }
  ]
}
```

Fetch products:

```http
GET /api/product/search-name?name=Gaming%20Mouse%20Logitech%20G502
GET /api/product/search-category?category=GAMING
GET /api/product/search-uuid?uuid=8a28afbb-2fc1-48dc-b592-7ca21f369e96
```

Update a product:

```http
PUT /api/product
Content-Type: application/json
```

Delete a product:

```http
DELETE /api/product?uuid=8a28afbb-2fc1-48dc-b592-7ca21f369e96
```

### Orders

Base path:

```text
/api/web-order
```

Create an order:

```http
POST /api/web-order
Content-Type: application/json
```

Example request shape:

```json
{
  "uuid": "0c2b7dd6-f381-4a80-adcb-5f5e2e02ddfa",
  "webUser": {
    "id": 1
  },
  "orderItems": [
    {
      "product": {
        "id": 1
      },
      "quantity": 2
    }
  ],
  "totalPrice": 99.98,
  "quantity": 2
}
```

Fetch, update, and delete orders:

```http
GET /api/web-order?uuid=0c2b7dd6-f381-4a80-adcb-5f5e2e02ddfa
PUT /api/web-order
DELETE /api/web-order?uuid=0c2b7dd6-f381-4a80-adcb-5f5e2e02ddfa
```

Fetch order items:

```http
GET /api/web-order/list?uuid=0c2b7dd6-f381-4a80-adcb-5f5e2e02ddfa
```

Note: `WebOrderController` currently needs its `WebOrdersService` field injected before these endpoints can work reliably. The field should be `final` or constructor-injected explicitly.

## Notes

- Local runtime env files are ignored by Git.
- The GitHub Actions workflow builds the project on pushes and pull requests to `master`.
- The repository remote has moved to `https://github.com/KonstantinosKosfi/PcShop.git`; older remotes may still redirect.

## Author

[@KonstantinosKosfi](https://github.com/KonstantinosKosfi)
