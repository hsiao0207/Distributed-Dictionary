# Distributed Dictionary System

A highly concurrent distributed dictionary service built with Java Sockets (Client-Server architecture). Supports multiple users concurrently querying, adding, updating, and deleting words. Uses a Read-Write Lock mechanism to ensure data consistency and high throughput.

## 📖 Project Introduction

This project implements a distributed dictionary system with a Graphical User Interface (GUI). The server can handle concurrent requests from multiple clients and utilizes fine-grained lock mechanisms (Lock Granularity) to prevent race conditions. The communication protocol uses a custom JSON-over-TCP format, ensuring lightweight and scalable network transmission.

**Key Features:**
* 🔍 **Query Words**: Supports highly concurrent read operations.
* ➕ **Add Words/Meanings**: Thread-safe write operations.
* 📝 **Update/Delete Words**: Modifies existing word meanings or removes words completely.
* 📊 **Server Dashboard**: A Server GUI to monitor the number of active connections and operation logs in real-time.

---

## 🏗️ System Architecture

1. **Networking Model**:
   * Uses **TCP Sockets** to provide a reliable byte-stream connection.
   * **Thread-per-connection** architecture: The main thread listens for incoming connections via `ServerSocket` and spawns a dedicated `Thread` to handle each client.

2. **Application Protocol**:
   * Custom Request-Response protocol.
   * Data Serialization: Uses `Gson` to serialize/deserialize custom Java objects (`DictionaryMessage`) into JSON strings over the Socket stream.

3. **Concurrency Control & Thread Safety**:
   * Utilizes `ReentrantReadWriteLock` to implement Read-Write separation.
   * **Read Operations (QUERY)**: Acquires a `readLock`, allowing multiple clients to query the dictionary "simultaneously" to maximize read performance.
   * **Write Operations (ADD/REMOVE/UPDATE)**: Acquires a `writeLock` to ensure mutual exclusion when modifying the dictionary, preventing data corruption.

---

## 🛠️ Tech Stack

* **Programming Language**: Java
* **Networking**: Java Sockets (TCP)
* **Concurrency**: `java.lang.Thread`, `ReentrantReadWriteLock`, `AtomicInteger`
* **GUI**: Java Swing
* **Serialization**: Gson (JSON)

---

## 🚀 Installation & Execution

### Prerequisites
* JDK 8 or higher.
* The `lib/gson-2.11.0.jar` dependency is included in the project.

### Method 1: Run with pre-compiled JAR files (Recommended)

If you already have the compiled `.jar` files, you can execute them directly via the command line:

**1. Start the Server**
You need to specify the port and the path to the dictionary data file (`dict.txt` is provided in the project).
```bash
java -jar DictionaryServer.jar <port> <dictionary-path>

# Example:
java -jar DictionaryServer.jar 8888 dict.txt
```

**2. Start the Client**
You need to specify the server's IP address, port, and a simulated delay (in milliseconds, used to test concurrency control).
```bash
java -jar DictionaryClient.jar <server-address> <server-port> <sleep-duration>

# Example (connect to localhost, no delay):
java -jar DictionaryClient.jar localhost 8888 0
```

### Method 2: Compile and Run from Source

If you are starting from the source code, run the following commands in the project root directory:

**1. Compile the source code**
```bash
# MacOS / Linux
javac -cp ".:lib/gson-2.11.0.jar" src/Common/*.java src/Server/*.java src/Client/*.java

# Windows (Note the semicolon separator)
javac -cp ".;lib/gson-2.11.0.jar" src/Common/*.java src/Server/*.java src/Client/*.java
```

**2. Run the Server**
```bash
# MacOS / Linux
java -cp ".:lib/gson-2.11.0.jar" src.Server.DictionaryServerGUI 8888 dict.txt
```

**3. Run the Client**
```bash
# MacOS / Linux
java -cp ".:lib/gson-2.11.0.jar" src.Client.DictionaryClientGUI localhost 8888 0
```