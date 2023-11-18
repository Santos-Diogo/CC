# Draft sobre a implementação da transferência de ficheiros

## Objective: Implementing a system for Peer-2-Peer transfer.

## First Step: Simple transfer using the [UDP](https://en.wikipedia.org/wiki/User_Datagram_Protocol) protocol

### To conclude a transfer between two nodes using UDP we need to keep in mind the functionality UDP lacks and aspects we should consider when developing our application such as:

#### 1. Choosing a *BlockSize* or implement a dynamic one.
This is important since we need to consider the network's [MTU](https://en.wikipedia.org/wiki/Maximum_transmission_unit) (Maximum Transmission Unit). The MTU is the maximum size of a single data unit that can be transmitted over the network. Fragmentation can occur if your data size exceeds the MTU, leading to potential performance issues.

#### 2. Packet Loss and Retransmission. 
UDP does not guarantee reliable delivery, so you may need to implement your own mechanisms for detecting and handling packet loss.

### Addresssing this aspects:

#### 1. Choosing a *BlockSize* or implement a dynamic one.
For the sake of simplicity we chose to set a static BlockSize of 1kB. This size was chosen as a size enferior to a common MTU size of ethernet ports of 1.5Kb.
It's important to chose a BlockSize smaller than the average MTU because this reduces the chance of fragmentation. A smaller BlockSize also allows us to retransmit smaller Data Units. A smaller BlockSize introduces more overhead to the equation so it's important to chose a value that fits our needs.

On a side note, specifically for our aplication, a BlockSize of 1kB could potentially yeld better overall results throughput-wise than a 1.5kB size. This is because we are implementing a P2P protocol and we could achieve paralelism earlier using smaller blocks.

#### 2. Packet Loss and Retransmission.
Each packet sent invokes an ACK message that should be recieved by the data transmitter in a given time-window. Not recieving confirmation in that time window causes the data transmiter to retransmit the packet. The rate of retransmited packages is an indicator for the connection's stress.

The time window between sending a packet and waiting for an ack is dynamic and can be adjusted in accordance to the real time RTT. Noticeable variation in this RTT value is an indicator of Jittering and should be interpreted as a sign of connection stress.

#### 3. Duplicate Blocks

On the recieving side, packet duplication is handled by discarding duplicate packets. packets are identified with their block number.

## Second Step: Scalonation of file-providing nodes in the server

### Multiple nodes in the network will hold multiple blocks from the same file. It's important to find the less stressed nodes to ensure the fastest transfer to the client node.

Each node is responsible for reporting it's current throughput in terms of data transfer to the server. The server records both the current throughput and the maximum throughput ever recorded. The "available" throughput and the number of transfers the node is responsible for in that moment dictate a node's atractiveness to be escalonated.

The higher the throughput available and the lower the transfers the node is responsible for, the more likely the node is to be selected for a new transfer.