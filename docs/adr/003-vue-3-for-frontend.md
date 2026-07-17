# ADR-003: Vue 3 for the frontend

### Status

Accepted

### Context

This application requires a web-based user interface that can communicate with the Spring Boot backend. The interface will to include several nested components.

### Decision

Vue 3 to be used as the frontend framework. 

### Alternatives Considered

React and Angular were considered as alternatives. Angular carries significant overhead that is unnecessary for a project of this size. This is particularly true in combination with the Spring Boot backend, which also carries significant overhead. React JSX is highly flexible, but this flexibility comes with more decisions for things such as third-party library selections for routing and state.

### Rationale

Vue 3's Single File Components keep style, HTML template, and logic all in one file, making component development quick and maintainable. Vue 3's `v-model` two-way binding will handle form state concisely, which is important considering the number of forms required for the application (e.g., character creation, campaign creation, notes, session logs). Vue Router and Pinia are also first-party, well-documented, and lightweight tools that make routing and state management less complicated.

The sole developer has equivalent experience across all three frameworks, but Vue is preferred for the developer experience, which reduces cognitive overhead and supports faster iteration.

### Consequences

##### Positive consequences

- Single FIle Components will keep each view self-contained and make the codebase easier to navigate
- Vue Router and Pinia are first-party and well-integrated
- Builds to static files, which can be deployed independently to any static host or served from Spring Boot directly

##### Negative consequences

- Vue has a significantly smaller ecosystem than alternatives such as React. Third party component libraries and community resources are less abundant, so UI components will likely need custom implementation
- Vue 3's Composition API is meaningfully different than Vue 2's Option's API, so older documentation and online forum answers can be misleading
- Deploying frontend and backend separately means managing two deployment targets and configuring CORS correctly