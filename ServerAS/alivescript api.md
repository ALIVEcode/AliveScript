## AliveScript Api

#### Request

- Format:
  - first execution
    ```json5
    {
    "lines": [] // array of lines to be executed
    }
    ```
  - other executions
    ```json5
    {
    "idToken": ""
    }
    ```

#### Response

- Format:
    - execution completed:
      ```json5
      {
      "status": "complete",
      "result": [] // the list of action the program needs to be executed by the website
      }
      ```
    - execution ongoing
      ```json5
      {
      "status": "ongoing",
      "token": "UUID", // a token to resume execution
      "result": [] // the list of action the program needs to be executed by the website
      }
      ```
    - execution failed
      ```json5
      {
      "status": "failed",
      "message": "[error message]" //the reason the execution failed
      }
      ```