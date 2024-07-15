# Video Files Backend Service

This project implements a backend service for handling video file operations using Spring Boot and FFmpeg.

## Requirements

1. **Authentication:** All API calls require authentication using a static API token (`user@Auth`).
2. **File Upload:** Allow users to upload videos with configurable limits:
   - Maximum size: 25 MB
   - Minimum duration: 5 seconds
   - Maximum duration: 25 seconds
3. **Video Trimming:** Trim a video file (previously uploaded) based on start and end times.
4. **Video Merging:** Merge multiple video clips (previously uploaded) into a single video file.
5. **Link Sharing:** Generate and validate shared links with expiry times.
6. **Testing:** Implement unit tests and end-to-end tests.
7. **Database:** Use SQLite as the database and commit it to the repository.
8. **API Documentation:** Provide API documentation using Swagger or Postman Collection JSON format.

## Setup Instructions

### Prerequisites

- Java 11+
- Maven
- FFmpeg installed (referenced as `C:\ffmpeg\bin\ffmpeg.exe` in the code)

### Steps to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/mayank1365/Video_Files_Backend.git
   cd Video_Files_Backend

2. Build and Run the project
3. Access the API documentation:
  - Postman Collection: (https://api.postman.com/collections/33491797-b26bd73c-deab-4aff-8b0d-8b5374de98e1?access_key=PMAT-01J2V46R7923TM3CYCY1K37985)

## Endpoints:
1. Upload File 
* Method: POST
* URL: /upload
* Headers: Authorization: user@Auth
* Description: Upload a video file.
* Request Body: form-data with file parameter.
  
2. Download File 
* Method: GET
* URL: /download/{fileId}
* Headers: Authorization: user@Auth
* Description: Download a previously uploaded video file by its ID.
  
3. Trim Video 
* Method: POST
* URL: /trim
* Headers: Authorization: user@Auth
* Description: Trim a video file based on start and end times.
* Request Body: JSON object with fileId, startTime, and endTime.
4. Merge Videos
* Method: POST
* URL: /merge
* Headers: Authorization: user@Auth
* Description: Merge multiple video clips into a single video file.
* Request Body: JSON object with a list of fileIds.
5. Share Link
* Method: GET
* URL: /share/{fileId}
* Headers: Authorization: user@Auth
* Description: Generate a shareable link for a video file.
* Response: URL that expires after a set time.
6. Access Shared File
* Method: GET
* URL: /shared/{token}
* Headers: Authorization: user@Auth
* Description: Access a shared video file using a token.
* Response: Download the shared video file.

## Assumptions and Choices
- FFmpeg is assumed to be installed locally at C:\ffmpeg\bin\ffmpeg.exe.
- Token management (tokenStore) is handled in-memory (ConcurrentHashMap) for simplicity.
- Error handling focuses on basic exceptions; production code should have more robust error handling.

## References
 - Spring Boot Documentation : https://spring.io/projects/spring-boot
 - FFmpeg Documentation : https://ffmpeg.org/documentation.html

## Contributors
- Mayank Gupta

## Authorisation
- All API endpoints require an Authorization header with the value `Bearer user@Auth`. Ensure to include this token in every request to authenticate successfully.
