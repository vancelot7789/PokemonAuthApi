
# Pokemon Auth API
This is a RestAPI project created in Spring Boot with JWT authentication. It implements the basic CRUD operations and pagination, with tests written. The database used is PostgreSQL, running in a Docker container.

## Tools
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/postgresql-4169e1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

## Key Features
- Allow users to register and log in.
- Users will receive a JWT token upon logging in, and need this JWT token in the header to get authorized to call the API.
- Create, Read, Update, Delete (CRUD) operations for Pokemons and reviews.
- Read operations for multiple Pokemons and reviews with pagination.
  
## To Start
Run the docker-compose.yml
```
docker-compose up
```
## API Examples For Registration and Login

| Method   | URL                                      | Description                              |
| -------- | ---------------------------------------- | ---------------------------------------- |
| `POST`   | `/api/auth/register`                     | Create a new user                        |
| `POST`   | `/api/auth/login`                        | Log in with username and password        |

Payload to include when registering and logging in
```
{
  "name": ${user name},
  "password": ${user password}
}
```

## API Examples for CRUD for Pokemons and Reviews
You must put the JWT token in the headers in the below format in order to call the API

```
Key: Authorization
Value: Bearer ${THE TOKEN YOU GET WHEN LOGIN}
```

| Method   | URL                                      | Description                              |
| -------- | ---------------------------------------- | ---------------------------------------- |
| `POST`   | `/api/pokemons/create`                   | Create a new Pokemon                     |
| `PUT`    | `/api/pokemons/23/update`                | Update Pokemon with id #23               |
| `DELETE` | `/api/pokemons/23/delete`                | Delete Pokemon with id #23               |
| `GET`    | `/api/pokemons/23`                       | Retrieve Pokemon with id #23             |
| `GET`    | `/api/pokemons`                          | Retrieve all Pokemons                    |

Payload to include when creating and updating Pokemon (JSON format)
```
{
  "name": ${pokemon name},
  "type": ${pokemon type}
}
```

| Method   | URL                                      | Description                              |
| -------- | ---------------------------------------- | ---------------------------------------- |
| `POST`   | `/api/pokemons/23/reviews`               | Create a new review for Pokemon with id #23                    |
| `PUT`    | `/api/pokemons/23/update/reviews/10`     | Update review with id #10 which is associated with Pokemon id #23      |
| `DELETE` | `/api/pokemons/23/reviews/10`            | Delete review with id #10 which is associated with Pokemon id #23    |
| `GET`    | `/api/pokemons/23/reviews/10`            | Retrieve review with id #10 which is associated with Pokemon id #23                     |
| `GET`    | `/api/pokemons/23/reviews`               | Retrieve all reviews associated with Pokemon id #23 |

Payload to include when creating and updating review (JSON format)
```
{
  "title": ${review title},
  "content": ${review content},
  "stars": ${review stars}
}
```



# Entities Overview

This project is built around several key entities that form the backbone of the application's functionality, including CRUD operations, authentication, and authorization. These entities are `Pokemon`, `Review`, `Role`, and `UserEntity`, each playing a pivotal role in the application's structure and relationships.

### Pokemon Entity

The `Pokemon` entity represents a Pokemon character with attributes and a relationship with its reviews:

- **Id**: A unique identifier for each Pokemon, automatically generated.
- **Name**: The name of the Pokemon.
- **Type**: The type of the Pokemon.

This entity also maintains a one-to-many relationship with the `Review` entity, allowing a Pokemon to have multiple reviews.

#### Attributes:

- `id`: int - The primary key of the Pokemon.
- `name`: String - The name of the Pokemon.
- `type`: String - The type of the Pokemon.
- `reviews`: List<Review> - A collection of reviews associated with the Pokemon.

### Review Entity

The `Review` entity encapsulates a review for a Pokemon with attributes and a reference back to the associated Pokemon:

- **Id**: A unique identifier for each review, automatically generated.
- **Title**: The title of the review.
- **Content**: The content of the review.
- **Stars**: The star rating of the review.

This entity is linked to the `Pokemon` entity through a many-to-one relationship, indicating which Pokemon the review pertains to.

#### Attributes:

- `id`: int - The primary key of the review.
- `title`: String - The title of the review.
- `content`: String - The content of the review.
- `stars`: int - The star rating of the review.
- `pokemon`: Pokemon - A reference back to the associated Pokemon.

### Role Entity

The `Role` entity represents the role assigned to a user, critical for authorization purposes:

- **Id**: A unique identifier for each role, automatically generated.
- **Name**: The name of the role.

#### Attributes:

- `id`: int - The primary key of the role.
- `name`: String - The name of the role.

### UserEntity

The `UserEntity` represents a user of the application, including authentication and authorization details:

- **Id**: A unique identifier for each user, automatically generated.
- **Username**: The username of the user.
- **Password**: The password of the user.

This entity maintains a many-to-many relationship with the `Role` entity, facilitating complex authorization schemes by associating users with roles.

#### Attributes:

- `id`: int - The primary key of the user.
- `username`: String - The username of the user.
- `password`: String - The password of the user.
- `roles`: List<Role> - A collection of roles associated with the user.

### Entity Relationship

The relationship between these entities is designed to reflect the complex interactions within the application:

- Each `Pokemon` can have multiple `Reviews`, but each `Review` is associated with only one `Pokemon`.
- `UserEntity` is associated with multiple `Roles` through a many-to-many relationship, enabling the assignment of multiple roles to a user and vice versa.

This structure supports a robust and flexible framework for managing Pokemons, reviews, user authentication, and authorization within the application.
