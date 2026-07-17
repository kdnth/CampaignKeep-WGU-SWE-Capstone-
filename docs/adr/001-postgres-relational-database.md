# ADR-001: PostgreSQL as the relational database

### Status

Accepted

### Context

This application requires persistent storage for user-owned data including *campaigns, characters, notes, and session logs*. The data is relational by nature, characters belong to campaigns, items belong to characters, and users have roles within campaigns.

### Decision

PostgreSQL to be used as the relational database.

### Alternatives Considered

MySQL, SQLite3, and MongoDB were all considered as prospective alternatives. MongoDB was not chosen because it is not relational. MySQL and SQLite3 both lack support for custom Enum types, which are helpful for things such as magic schools and damage types.

### Rationale

PostgreSQL is the right choice for this application because of its object-oriented nature and support for custom types, which are helpful for enforcing things such as schools of magic or damage types. 

### Consequences

##### Positive Consequences 

- Fully integrated support in Java Spring Boot with automatic connection pooling
- Custom Enum type support

##### Negative Consequences

- High memory consumption per connection

- Requires a hosted instance at deployment time (available on Railway and Render free tiers)