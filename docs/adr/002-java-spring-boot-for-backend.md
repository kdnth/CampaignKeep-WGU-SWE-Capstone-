# ADR-002: Java and Spring Boot for the backend

### Status

Accepted

### Context

This application requires secure data processing, storage, and management for user authorization, user data persistance, and business logic. 

### Decision

Java Spring Boot

### Alternatives Considered

Node.js, Django, and Go were all considered as alternatives. A significant drawback for each alternative framework is the developer's lack of experience with all three. Node.js would allow a single language (JavaScript) to be used across the stack. Both Node.js and Django are good choices for rapid prototype development, and Go excels in high-performance runtime and memory efficiency, but they all lack the out-of-the-box security, an integrated data access layer, and maintainability offered by other options. Additionally, neither Go or JavaScript (or TypeScript) would natively support the OOP concept requirements of the capstone. 

### Rationale

Spring Boot has several advantages. It uses Java, a strongly typed, object-oriented language that supports the capstone's required OOP concepts (inheritance, polymorphism, and encapsulation). Spring Boot also provides out-of-the-box features such as auto-configuration, a REST API engine, and security through Spring Security. These features all support the most significant deciding factor, which is the developer's prior experience with the framework. 

### Consequences

##### Positive Consequences

- Out-of-the-box REST API support and security through Spring Web and Spring Security
- Easily scalable with smaller memory scaling than alternatives like Node.js
- Type safety will avoid runtime errors
- Automatic connection pooling handling with HikariCP

##### Negative Consequences 

- Java Spring Boot carries significant resource overhead, with a high memory footprint and longer startup times. 
- Prototyping is less rapid than alternatives
- Separate frontend and backend languages