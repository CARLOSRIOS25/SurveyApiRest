# 📊 Surveys API REST

Proyecto de práctica para consolidar conocimientos en Spring Boot, seguridad, tests y buenas prácticas de desarrollo.

---

## **🔹 Tecnologías y buenas prácticas usadas**
- **Spring Boot ⚡**: Framework principal para la API REST.  
- **Spring Security 🔒**: Configuración de seguridad y protección de endpoints.  
- **JWT 🛡️**: Manejo de tokens para autenticación y autorización.  
- **Tests Unitarios y de Integración 🧪**: Uso de `@WebMvcTest`, `MockMvc`, `JsonAssert` y pruebas de integración con base de datos.  
- **Manejo de DTOs 📦**: Separación de entidades de los objetos que se exponen en la API.  
- **Manejo de excepciones ⚠️**: Uso de `@RestControllerAdvice` y excepciones personalizadas.  
- **MySQL 🐬 con Docker 🐳**: Contenerización de base de datos y conexión desde Spring Boot.  
- **Docker Compose 🚀**: Orquestación de backend y base de datos.  

---

## **📌 Configuración del proyecto**

### 1️⃣ Requisitos previos
- Docker y Docker Compose instalados  
- Java 17+ o Amazon Corretto 24 (según tu configuración)  
- Maven o Gradle (según uses)

---

### 2️⃣ Empaquetar la app y levantar los contenedores
```bash
mvn package -Dmaven.test.skip=true
docker compose up -d --build
```
uso de endpoints:

<img width="1458" height="841" alt="image" src="https://github.com/user-attachments/assets/244e367d-71b6-434d-97df-394561b901b9" />

- loginAuth(post): /auth/login

- registerAuth(post): /auth/register

- createSurvey(post): /api/surveys

- createQuestion(post): /api/surveys/{surveyTitle}/questions

- getAllSurveys(get): /api/surveys

- getSurveyByTitle(get): /api/surveys/{surveyTitle}

- getAllSurveyQuestions(get): /api/surveys/{surveyTitle}/questions

- getSurveyQuestionById(get): /api/surveys/{surveyTitle}/questions/{id}

- updateSurvey(put): /api/surveys/{surveyTitle}

- updateSurveyQuestion(put): /api/surveys/{surveyTitle}/questions/{id}

- deleteSurvey(del): /api/surveys/{id}

- deleteQuestion(del): /api/surveys/{surveyTitle}/questions/{id}

---

Login/Register:
```json
{
	"username" : "",
	"password" : ""
}
```
---

CreateSurvey:
```json
{
	"title": "",
	"description": "",
	"questions": [
		{
			"description": "",
			"correctAnswer": "",
			"options": [
				"",
				""
			]
		},
		{
			"description": "",
			"correctAnswer": "",
			"options": [
				"",
				""
			]
		}
	]
}
```
---

CreateQuestion:
```json
{
	"description": "",
	"correctAnswer": "",
	"options": [
		"",
		"",
		""
	]
}
```
---

UpdateSurvey:
```json
{
  "title": "",
  "description": ""
}
```
---

UpdateQuestion:
```json
{
	"description": "",
	"correctAnswer": "",
	"options": [
		"",
		"",
		""
	]
}
```
