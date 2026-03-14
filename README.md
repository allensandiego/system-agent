# System Agent

A Spring Boot-based REST service designed to expose system metrics (CPU, Memory, etc.) and execute remote commands. This project is specifically tailored for integration with **Home Assistant**, allowing you to monitor and control your desktop PCs via `REST Commands` and `REST Sensors`.

## Features
- **System Monitoring:** Access real-time CPU, Memory, and System statistics via JSON.
- **Remote Control:** Execute system commands (Shutdown, Restart, etc.) through REST endpoints.
- **Security:** Protected by API Key authentication.
- **User Interface:** Minimalist System Tray icon for visibility and status.

---

## Prerequisites
- **Java 17 or higher** (OpenJDK recommended).
- **Maven** (optional, `mvnw` wrapper is included).

---
## Installation

### Recommended: GitHub Releases
Download the pre-compiled `.jar` file from the [Releases](https://github.com/allensandiego/system-agent/releases) page. This is the easiest method and doesn't require Maven or building the code yourself.

1.  **Download:** Get the latest `System-Agent.jar` from the Releases page.
2.  **Run:** Use Java to start it:
    ```bash
    java -jar System-Agent.jar
    ```

### Manual Build (Alternative)
If you prefer to build from source:

#### Windows
1. **Clone the repository:**
...
   ```bash
   git clone https://github.com/allensandiego/system-agent.git
   cd system-agent
   ```
2. **Build the application:**
   ```bash
   ./mvnw clean package
   ```
3. **Run as a Background Process:**
   You can run the JAR directly:
   ```bash
   java -jar target/System-Agent.jar
   ```
   *Note: To run on startup, you can create a shortcut in the `shell:startup` folder or use the included WinSW configuration to install it as a Windows Service.*

### Linux
1. **Clone and Build:**
   ```bash
   git clone https://github.com/allensandiego/system-agent.git
   cd system-agent
   ./mvnw clean package
   ```
2. **Run the JAR:**
   ```bash
   java -jar target/System-Agent.jar
   ```
3. **Setup as a Systemd Service (Recommended):**
   Create `/etc/systemd/system/system-agent.service`:
   ```ini
   [Unit]
   Description=System Agent REST Service
   After=network.target

   [Service]
   User=<your-username>
   ExecStart=/usr/bin/java -jar /path/to/system-agent/target/System-Agent.jar
   SuccessExitStatus=143
   Restart=always
   RestartSec=10

   [Install]
   WantedBy=multi-user.target
   ```
   Then enable and start:
   ```bash
   sudo systemctl enable system-agent
   sudo systemctl start system-agent
   ```

---

## Home Assistant Integration

### 1. REST Sensor (Monitoring)
Add this to your `configuration.yaml` to monitor CPU usage:
```yaml
sensor:
  - platform: rest
    name: "Desktop CPU Usage"
    resource: http://<PC_IP>:8080/api/v1/system/processor
    headers:
      X-API-KEY: your_api_key_here
    value_template: "{{ value_json.systemCpuLoad }}"
    unit_of_measurement: "%"
```

### 2. REST Command (Control)
Add this to trigger a shutdown:
```yaml
rest_command:
  shutdown_desktop:
    url: "http://<PC_IP>:8080/api/v1/system/shutdown"
    method: POST
    headers:
      X-API-KEY: your_api_key_here
```

---

## Configuration
Update `src/main/resources/application.properties` to change the port or security settings:
- `server.port=8080`
- `server.servlet.context-path=/api/v1`
