# Database Migrations

This directory contains versioned SQL migration scripts for the SmartDoc application.

## Naming Convention
Scripts should follow the Flyway naming convention:
`V<Version>__<Description>.sql`
Example: `V1__fix_inventory_calculation.sql`

## How to Apply
Currently, these scripts are applied manually or via a custom runner.
To apply a script using Docker:
```powershell
Result: type path/to/script.sql | docker exec -i <container_name> psql -U <user> -d <database>
```

## Future Improvement
We recommend integrating **Flyway** or **Liquibase** into the Spring Boot application to automatically apply these migration scripts on startup. This ensures all environments (dev, test, prod) are always in sync with the required database schema/logic.
