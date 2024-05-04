# Totally-Ordered-RMI

## Totally Ordered RMI with Raft Node

This project demonstrates a totally ordered Remote Method Invocation (RMI) system using the Raft consensus algorithm. The goal is to achieve reliable and consistent communication between distributed nodes.

### Features

- **Totally Ordered Messaging:** Messages are delivered in the same order across all nodes.
- **Raft Consensus Algorithm:** Ensures consistency and fault tolerance.
- **Java RMI:** Utilizes Java’s built-in RMI framework for communication.
- **File System Operations:** Allows users to upload, delete, download, and search files.

### Getting Started

To get started with this project, follow these steps:

1. Clone this repository:

```bash
git clone https://github.com/YasoKarim/Totally-Ordered-RMI.git

2. Navigate to the project directory:

```bash
cd Totally-Ordered-RMI 

2. Run multiple Parallels:
- Click Run ➡️ Edit Configurations….
- Select the configuration for the file that requires multiple instances.
- Check the Allow parallel run option.
- Click Apply.

3. Specify the Input:
- You choose a number to do one of the operations(Delete, Upload, Download and Search)

Each node maintains a local logical clock value.
o When a node wants to send a request, it updates its logical clock and 
multicasts the request (timestamped with its logical clock value) to all other 
nodes.
o Upon receiving a request, the recipient puts it in a queue ordered by 
requests’ timestamp (a tie breaker will be needed for requests with the same 
timestamp e.g. requestId), then the recipient multicasts an ACK to other 
nodes (ACKs are not queued).
o A request is executed only when it has been ACKed by all nodes.
o For the Upload and Delete requests, all nodes should execute the requests.
o For the Download and Search requests only one node (Leader) should 
execute the request. (A leader is selected randomly). 

