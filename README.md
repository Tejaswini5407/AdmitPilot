# AdmitPilot

### AP EAPCET College Predictor

AdmitPilot is a full-stack web application that helps students explore engineering colleges based on their **AP EAPCET rank, category, gender, and preferred branches** using historical cutoff data.

## Features

- Enter AP EAPCET rank
- Select category and gender
- Select multiple preferred branches
- Predict eligible colleges using historical cutoff data
- View results branch-wise
- Search, filter, and sort predicted colleges
- View detailed college information
- View historical cutoff information
- View available NIRF and placement information
- Visit official college websites
- Download prediction results as CSV
- Download prediction results as PDF
- Responsive design for desktop and mobile

## How Prediction Works

AdmitPilot compares the student's rank with historical closing ranks for the selected:

- Category
- Gender
- Branch
- Year
- Round

A college is considered eligible when:

`Student Rank <= Historical Closing Rank`

The application uses database-driven filtering rather than hard-coded college recommendations.

## Tech Stack

### Frontend

- React
- Vite
- JavaScript
- HTML
- CSS

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- REST APIs
- Maven

### Database

- PostgreSQL

### Data

- AP EAPCET historical cutoff data
- Verified college profile information
- NIRF information
- Placement information
- Official college website information

## System Architecture

```text
Student
   |
   v
React / Vite Frontend
   |
   | REST API
   v
Spring Boot Backend
   |
   | JPA / Hibernate
   v
PostgreSQL Database
   |
   v
Historical Cutoffs
College Data
NIRF Data
Placement Data