# Defiance Spring Boot API Documentation

Base URL: `http://localhost:8080`

## User

| Model | Endpoint | Request Type | Sample Request Body | Sample Response |
| --- | --- | --- | --- | --- |
| User | `/api/users` | POST | `{ "firstName": "Sample", "middleName": "", "lastName": "User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "isAdmin": false }` | `{ "id": 1, "firstName": "Sample", "middleName": "", "lastName": "User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "isAdmin": false }` |
| User | `/api/users` | GET | N/A | `[ { "id": 1, "firstName": "Sample", "middleName": "", "lastName": "User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "isAdmin": false } ]` |
| User | `/api/users/{id}` | GET | N/A | `{ "id": 1, "firstName": "Sample", "middleName": "", "lastName": "User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "isAdmin": false }` |
| User | `/api/users/{id}` | PUT | `{ "firstName": "Sample", "middleName": "", "lastName": "User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "isAdmin": false }` | `{ "id": 1, "firstName": "Sample", "middleName": "", "lastName": "User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "isAdmin": false }` |
| User | `/api/users/{id}` | DELETE | N/A | `204 No Content` |

## Product

| Model | Endpoint | Request Type | Sample Request Body | Sample Response |
| --- | --- | --- | --- | --- |
| Product | `/api/products` | POST | `{ "name": "Basic Tee", "price": 600, "image": "image.jpg", "category": "tshirts", "isActive": true, "stock": 10 }` | `{ "id": 1, "name": "Basic Tee", "price": 600, "image": "image.jpg", "category": "tshirts", "isActive": true, "stock": 10 }` |
| Product | `/api/products` | GET | N/A | `[ { "id": 1, "name": "Basic Tee", "price": 600, "image": "image.jpg", "category": "tshirts", "isActive": true, "stock": 10 } ]` |
| Product | `/api/products/{id}` | GET | N/A | `{ "id": 1, "name": "Basic Tee", "price": 600, "image": "image.jpg", "category": "tshirts", "isActive": true, "stock": 10 }` |
| Product | `/api/products/{id}` | PUT | `{ "name": "Basic Tee", "price": 600, "image": "image.jpg", "category": "tshirts", "isActive": true, "stock": 10 }` | `{ "id": 1, "name": "Basic Tee", "price": 600, "image": "image.jpg", "category": "tshirts", "isActive": true, "stock": 10 }` |
| Product | `/api/products/{id}` | DELETE | N/A | `204 No Content` |

## Order

| Model | Endpoint | Request Type | Sample Request Body | Sample Response |
| --- | --- | --- | --- | --- |
| Order | `/api/orders` | POST | `{ "userId": 1, "fullName": "Sample User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "paymentMethod": "gcash", "receiptPath": "receipt.jpg", "total": 1200, "status": "pending_payment" }` | `{ "id": 1, "userId": 1, "fullName": "Sample User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "paymentMethod": "gcash", "receiptPath": "receipt.jpg", "total": 1200, "status": "pending_payment" }` |
| Order | `/api/orders` | GET | N/A | `[ { "id": 1, "userId": 1, "fullName": "Sample User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "paymentMethod": "gcash", "receiptPath": "receipt.jpg", "total": 1200, "status": "pending_payment" } ]` |
| Order | `/api/orders/{id}` | GET | N/A | `{ "id": 1, "userId": 1, "fullName": "Sample User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "paymentMethod": "gcash", "receiptPath": "receipt.jpg", "total": 1200, "status": "pending_payment" }` |
| Order | `/api/orders/{id}` | PUT | `{ "userId": 1, "fullName": "Sample User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "paymentMethod": "gcash", "receiptPath": "receipt.jpg", "total": 1200, "status": "pending_payment" }` | `{ "id": 1, "userId": 1, "fullName": "Sample User", "email": "sample@example.com", "phone": "09123456789", "addressLine": "123 Street", "barangay": "Barangay 1", "city": "Imus", "province": "Cavite", "postalCode": "4103", "paymentMethod": "gcash", "receiptPath": "receipt.jpg", "total": 1200, "status": "pending_payment" }` |
| Order | `/api/orders/{id}` | DELETE | N/A | `204 No Content` |

## Order Item

| Model | Endpoint | Request Type | Sample Request Body | Sample Response |
| --- | --- | --- | --- | --- |
| Order Item | `/api/order-items` | POST | `{ "orderId": 1, "productId": 1, "qty": 2, "price": 600, "subtotal": 1200 }` | `{ "id": 1, "orderId": 1, "productId": 1, "qty": 2, "price": 600, "subtotal": 1200 }` |
| Order Item | `/api/order-items` | GET | N/A | `[ { "id": 1, "orderId": 1, "productId": 1, "qty": 2, "price": 600, "subtotal": 1200 } ]` |
| Order Item | `/api/order-items/{id}` | GET | N/A | `{ "id": 1, "orderId": 1, "productId": 1, "qty": 2, "price": 600, "subtotal": 1200 }` |
| Order Item | `/api/order-items/{id}` | PUT | `{ "orderId": 1, "productId": 1, "qty": 2, "price": 600, "subtotal": 1200 }` | `{ "id": 1, "orderId": 1, "productId": 1, "qty": 2, "price": 600, "subtotal": 1200 }` |
| Order Item | `/api/order-items/{id}` | DELETE | N/A | `204 No Content` |

## Cart Item

| Model | Endpoint | Request Type | Sample Request Body | Sample Response |
| --- | --- | --- | --- | --- |
| Cart Item | `/api/cart-items` | POST | `{ "cartId": "cart_123", "productId": 1, "qty": 1, "price": 600, "subtotal": 600 }` | `{ "id": 1, "cartId": "cart_123", "productId": 1, "qty": 1, "price": 600, "subtotal": 600 }` |
| Cart Item | `/api/cart-items` | GET | N/A | `[ { "id": 1, "cartId": "cart_123", "productId": 1, "qty": 1, "price": 600, "subtotal": 600 } ]` |
| Cart Item | `/api/cart-items/{id}` | GET | N/A | `{ "id": 1, "cartId": "cart_123", "productId": 1, "qty": 1, "price": 600, "subtotal": 600 }` |
| Cart Item | `/api/cart-items/{id}` | PUT | `{ "cartId": "cart_123", "productId": 1, "qty": 1, "price": 600, "subtotal": 600 }` | `{ "id": 1, "cartId": "cart_123", "productId": 1, "qty": 1, "price": 600, "subtotal": 600 }` |
| Cart Item | `/api/cart-items/{id}` | DELETE | N/A | `204 No Content` |
