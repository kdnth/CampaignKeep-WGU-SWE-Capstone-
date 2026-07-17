# ADR-004: JWT for authentication

### Status

Accepted

### Context

The application requires both authentication and authorization. The architecture is a Vue 3 SPA communicating with a Spring Boot REST API; separate processes, potentially on separate hosts. The backend will need a way to verify identity on every request without the frontend and backend sharing server memory.

### Alternatives Considered

**Server-side sessions**: Traditional approach. Server stores session state and give client a session ID cookie. Awkward for a decoupled SPA + REST API. 

**OAuth2/Social Logic**: This approach delegates auth to a third party like Google. Password management is eliminated entirely, but introduces an external dependency.

**Spring Security's default form login**: This is designed for server-rendered apps, not REST APIs, so it's not appropriate for this use case

### Rationale

JWT is the standard choice for a stateless SPA + REST API authentication. The token is issued at login, stored client-side, and sent with every request in the Authorization header. Spring Security has first-party JWT support and the token payload can carry the user's role, so auth checks don't require a DB lookup on every request. 

### Consequences

##### Positive

- It is stateless, no server-side session storage is required
- Role claims are embedded in the token support TBAC without any extra DB queries per request
- Well-supported in Spring Security
- Works naturally across separate frontend/backend deployments

##### Negative

- Tokens can't be invalidated server-side without additional infrastructure
- Token expiry requires a decision
- The signing secret must be kept out of the codebase
- Payload is encoded, not encrypted

