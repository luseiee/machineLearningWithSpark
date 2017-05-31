> 计算机网络第五版第一章，第五章，第六章的习题解答。编号是按照中文版图书来的，题目是复制的英文版图书。答案经过本人验证，比较可靠。

# 第一章：引言

> (1) Imagine that you have trained your St. Bernard, Bernie, to carry a box of three 8mm tapes instead of a flask of brandy. (When your disk fills up, you consider that an emergency.) These tapes each contain 7 gigabytes. The dog can travel to your side, wherever you may be, at 18 km/hour. For what range of distances does Bernie have a higher data rate than a transmission line whose data rate (excluding overhead) is 150 Mbps?

Sol. 

`7 * 1024 * 8 * 3 / (d / 5) = 150`  
`d = 5734.4m`

Wrong!

`1 kbps = 1000 bit/s`  
1024 should be 1000  
`d = 5600m`  


> (2) An alternative to a LAN is simply a big timesharing system with terminals for all users. Give two advantages of a client-server system using a LAN.

Sol.

(1) 可以更好地利用网络资源，timesharing system容易造成资源浪费  
(2) 构建简单，客户机和服务机上各运行一个进程即可


> (3) The performance of a client-server system is influenced by two network factors: the bandwidth of the network (how many bits/sec it can transport) and the latency (how many seconds it takes for the first bit to get from the client to the server). Give an example of a network that exhibits high bandwidth and high latency. Then give an example of one with low bandwidth and low latency.

Sol.

(1). 下载大型文件，建立连接需要很久，一旦建立下载很快  
(2). 语音通话，不能有高延迟

A transcontinental fiber link might have many gigabits/sec of bandwidth, but the latency will also be high due to the speed of light propagation over thousands of kilometers. In contrast, a 56-kbps modem calling a computer in the same building has low bandwidth and low latency.

> (4) Besides bandwidth and latency, what other parameter is needed to give a good characterization of the quality of service offered by a network used for digitized voice traffic?

Sol.

(1) 丢包率  
(2) 安全性  
(3) 断点传输  
(4) 对语音通话而言，统一的传输时间也很重要  

> (5) A factor in the delay of a store-and-forward packet-switching system is how long it takes to store and forward a packet through a switch. If switching time is 10 μsec, is this likely to be a major factor in the response of a client-server system where the client is in New York and the server is in California? Assume the propagation speed in copper and fiber to be 2/3 the speed of light in vacuum.

Sol.

`2000km / 200000km/s = 10ms`

It won't be a major factor.

> (6) A client-server system uses a satellite network, with the satellite at a height of 40,000 km. What is the best-case delay in response to a request?

Sol.

`40,000 * 2 / 300000 = 267ms`

> (7) In the future, when everyone has a home terminal connected to a computer network, instant public referendums on important pending legislation will become possible. Ultimately, existing legislatures
could be eliminated, to let the will of the people be expressed directly. The positive aspects of such a
direct democracy are fairly obvious; discuss some of the negative aspects.

Sol.

安全性存在问题

> (8) A collection of five routers is to be connected in a point-to-point subnet. Between each pair of routers,
the designers may put a high-speed line, a medium-speed line, a low-speed line, or no line. If it takes 100 ms of computer time to generate and inspect each topology, how long will it take to inspect all of them?

Sol.

`4 ^ (4 + 3 + 2 + 1) * 100ms = more than one day`

> (9) A disadvantage of a broadcast subnet is the capacity wasted when multiple hosts attempt to access the channel at the same time. As a simplistic example, suppose that time is divided into discrete slots, with each of the n hosts attempting to use the channel with probability p during each slot. What fraction of the slots are wasted due to collisions?

Sol.

`P = 1 - n * p * (1 - p) ^ (n - 1) - p ^ n`

> (10) What are two reasons for using layered protocols?

Sol.

(1) 使得层与层之间不会影响，一层内的协议更新不会使得整个网络连接发生变化  
(2) 把大的任务分细，明确各层提供的服务

> (11) The president of the Specialty Paint Corp. gets the idea to work with a local beer brewer to produce an
invisible beer can (as an anti-litter measure). The president tells her legal department to look into it, and they in turn ask engineering for help. As a result, the chief engineer calls his counterpart at the other company to discuss the technical aspects of the project. The engineers then report back to their respective legal departments, which then confer by telephone to arrange the legal aspects. Finally, the two corporate presidents discuss the financial side of the deal. Is this an example of a multilayer protocol in the sense of the OSI model?

Sol.

除了第一层（最底层，这里是engineer），高层之间不能有直接通信。

> (12)  What is the principal difference between connectionless communication and connection-oriented communication?

Sol.

面向连接的服务是先建立连接再通信。无连接服务是在传输时候带上目标地址，然后交由网络去通信。

> (13) Two networks each provide reliable connection-oriented service. One of them offers a reliable byte stream and the other offers a reliable message stream. Are these identical? If so, why is the distinction made? If not, give an example of how they differ.

Sol.

message的话每条信息有确定边界，而byte stream则没有确定边界。

> (14) What does ''negotiation'' mean when discussing network protocols? Give an example.

Sol.

协商就是建立一个双方接受的通信规则。

> (15) Which of the OSI layers handles each of the following:
a. (a) Dividing the transmitted bit stream into frames.
b. (b) Determining which route through the subnet to use.

Sol.

Transmission Layer and Network Layer

Wrong!

Data link layer and Network layer

这里很重要，数据是在data link层被划分为帧（frame）的

> (16) If the unit exchanged at the data link level is called a frame and the unit exchanged at the network level
is called a packet, do frames encapsulate packets or do packets encapsulate frames? Explain your
answer.

Sol.

高层的应该被封到低层里去。

> (17) A system has an n-layer protocol hierarchy. Applications generate messages of length M bytes. At each
of the layers, an h-byte header is added. What fraction of the network bandwidth is filled with headers?

Sol.

`hn / (M + hn)`

> (18) List two ways in which the OSI reference model and the TCP/IP reference model are the same. Now list
two ways in which they differ.

Sol.

OSI:  
Application       
Representation    
Session           
Transmission        
Network           
Data link         
Physical          

TCP/IP:  
Application: HTTP, SMTP, RTP, DNS  
Transmission: TCP, UDP  
internet: IP (Internet Protocal), ICMP  
Link: DSL, SONET, 802.11, Ethernet  

> (19) What is the main difference between TCP and UDP?

Sol.

TCP is connection oriented, reliable, fast.  
UDP is connectionless oriented, unreliable, slow.  

> (20) When a file is transferred between two computers, two acknowledgement strategies are possible. In the first one, the file is chopped up into packets, which are individually acknowledged by the receiver, but the file transfer as a whole is not acknowledged. In the second one, the packets are not acknowledged
individually, but the entire file is acknowledged when it arrives. Discuss these two approaches.

Sol.

If the network is reliable, use the second one.

> (21) How long was a bit on the original 802.3 standard in meters? Use a transmission speed of 10 Mbps and
assume the propagation speed in coax is 2/3 the speed of light in vacuum.

Sol.

802.3是以太网，这里计算出来是20米

> (22) An image is 1024 x 768 pixels with 3 bytes/pixel. Assume the image is uncompressed. How long does it
take to transmit it over a 56-kbps modem channel? Over a 1-Mbps cable modem? Over a 10-Mbps Ethernet? Over 100-Mbps Ethernet?

Sol.

337s, 19s, 1.9s, 0.19s

> (23) Ethernet and wireless networks have some similarities and some differences. One property of Ethernet is that only one frame at a time can be transmitted on an Ethernet. Does 802.11 share this property with Ethernet? Discuss your answer.

Sol.

Wireless networks also transmit one frame at a time, but determining when one can send is more difficult. Ethernet is a broadcast bus so the sender can just listen to see if there is any traffic before sending. Due to range issues with wireless, a sender cannot be sure that there are not other transmissions just because it cannot "hear" other transmissions. Wireless senders use either a central base station or use methods discussed in Chapter 4 to avoid collisions.

> (24) Wireless networks are easy to install, which makes them inexpensive since installation costs usually far overshadow equipment costs. Nevertheless, they also have some disadvantages. Name two of them.

Sol.

无法控制范围，容易被人利用。任何人可以接收到传送中的包。  
此外，传输更慢。

> (25) List two advantages and two disadvantages of having international standards for network protocols.

Sol.

好处：统一的标准使得兼容性更好，成本降低。
坏处：不好的标准可能很难被淘汰，比如OSI比TCP／IP先进但没有被采用。

# 第五章：网络层

> (1) Give two example computer applications for which connection-oriented service is appropriate. Now give two examples for which connectionless service is best.

Sol.

面向连接服务：SSH，文件传输

无连接服务：视频通话，游戏在线对战，需要快速应答的服务一般需要无连接服务。

> (2) Are there any circumstances when connection-oriented service will (or at least should) deliver packets out of order? Explain.

Sol. 

接收端将无法正确接收数据，比如视频传输，顺序一错视频就乱了。

Wrong!

Circumstance是情况的意思，在terminal中ctrl-c应该被最先传送，而不是排在队尾。

> (3) Datagram subnets route each packet as a separate unit, independent of all others. Virtual-circuit subnets do not have to do this, since each data packet follows a predetermined route. Does this observation mean that virtual-circuit subnets do not need the capability to route isolated packets from an arbitrary source to an arbitrary destination? Explain your answer.

Sol.

并不是如此，虚电路网络对不同方向的数据包需要建立不同的虚电路，因此也要具备这样的能力。

补充：在建立虚电路的时候需要把setup package从任意端传输到任意接收方。

> (4) Give three examples of protocol parameters that might be negotiated when a connection is set up.

Sol.

IP协议，协商IP包的最大跳数，是否可以分段，还有出错时的处理方式。

> (5) Consider the following design problem concerning implementation of virtual-circuit service. If virtual circuits are used internal to the subnet, each data packet must have a 3-byte header and each router must tie up 8 bytes of storage for circuit identification. If datagrams are used internally, 15-byte headers are needed but no router table space is required. Transmission capacity costs 1 cent per 106 bytes, per hop. Very fast router memory can be purchased for 1 cent per byte and is depreciated over two years, assuming a 40-hour business week. The statistically average session runs for 1000 sec, in which time 200 packets are transmitted. The mean packet requires four hops. Which implementation is cheaper, and by how much?

Sol.

Virtual Circuits: `200 * 3 * 4 / 10 ^ 6 + 1000 * 8 * 5 / 2 / 52 / 40 / 3600`
Datagrams: `15 * 4 * 200 / 10 ^ 6`

> (6) Assuming that all routers and hosts are working properly and that all software in both is free of all errors, is there any chance, however small, that a packet will be delivered to the wrong destination?

Sol.

有可能，当有ip冲突或者传输过程中发生ip地址变更的情况。

Wrong!

答案是错误可能出在低层上，比如物理层

> (7) Give a simple heuristic for finding two paths through a network from a given source to a given destination that can survive the loss of any communication line (assuming two such paths exist). The routers are considered reliable enough, so it is not necessary to worry about the possibility of router crashes.

Sol.

先找出一条最短路径，再把这条路径删除，找出另一条最短路径。只要两条路径没有公共部分即可。

> (8) Consider the subnet of Fig.5-12(a). Distance vector routing is used, and the following vectors have just come in to router C: from B: (5, 0, 8, 12, 6, 2); from D: (16, 12, 6, 0, 9, 10); and from E: (7, 6, 3, 9, 0, 4). The measured delays to B, D, and E, are 6, 3, and 5, respectively. What is C's new routing table? Give both the outgoing line to use and the expected delay.

Sol.

这题是距离矢量算法。

```
  C 到 A 11 经过 B
  A 11 B
  B 6  B
  C 0  -
  D 3  D
  E 5  E
  F 8  B
```

> (9) If delays are recorded as 8-bit numbers in a 50-router network, and delay vectors are exchanged twice a second, how much bandwidth per (full-duplex) line is chewed up by the distributed routing algorithm? Assume that each router has three lines to other routers.

Sol.

每个路由器需要维护一张400bit的表，因此传输0.5s一次会浪费单个方向800bps的带宽

> (10) In Fig. 5-14 the Boolean OR of the two sets of ACF bits are 111 in every row. Is this just an accident here, or does it hold for all subnets under all circumstances?

Sol.

这题讲的是链路状态路由算法。

这个永远是对的，ACK标志标志表示来自哪里，它可能由两条路线过来，而发送标志表示要发送往哪里。

> (11) For hierarchical routing with 4800 routers, what region and cluster sizes should be chosen to minimize the size of the routing table for a three-layer hierarchy? A good starting place is the hypothesis that a solution with k clusters of k regions of k routers is close to optimal, which means that k is about the cube root of 4800 (around 16). Use trial and error to check out combinations where all three parameters are in the general vicinity of 16.

Sol.

这题讲的是层次路由。

层次可以分许多层，和网络中路由器的数量规模有关。

16 15 20

> (12) In the text it was stated that when a mobile host is not at home, packets sent to its home LAN are intercepted by its home agent on that LAN. For an IP network on an 802.3 LAN, how does the home agent accomplish this interception?

Sol.

家乡代理拥有主机的IP地址即可截获，有什么问题吗？

Conceivably it might go into promiscuous mode, reading all frames dropped onto the LAN, but this is very inefficient. Instead, what is normally done is that the home agent tricks the router into thinking it is the mobile host by re- sponding to ARP requests. When the router gets an IP packet destined for the mobile host, it broadcasts an ARP query asking for the 802.3 MAC-level ad- dress of the machine with that IP address. When the mobile host is not around, the home agent responds to the ARP, so the router associates the mobile user’s IP address with the home agent’s 802.3 MAC-level address.

上面是标准答案，实际上就是在ARP广播时相应家乡代理自己的MAC地址，实际上就是拥有了主机的IP地址嘛。

> (13) Looking at the subnet of Fig. 5-6, how many packets are generated by a broadcast from B, using a. (a)reverse path forwarding? b. (b)the sink tree?

Sol.

reverse path forwarding，路由器接收到广播包时，检查是否是自己给广播源发包的路径，是的话说明是从最优路径发过来的，这时给所有其他节点发包，最后算下来发了21次

sink tree是汇集树，是由B到所有节点最短路径的集合，这样发的话发14个包，每个树枝必会到一个节点，而且不会重复。

> (14)  Suppose that node B in Fig. 5-20 has just rebooted and has no routing information in
its tables. It suddenly needs a route to H. It sends out broadcasts with TTL set to 1, 2,
3, and so on. How many rounds does it take to find a route?

Sol.

这题考查自组织网络路由，就是路由器本身也在移动的情况。TTL分别设置为1，2，3是为了使搜索半径不断增大。由于B和H距离为3，因此TTL设置为3的时候可以发现，因此3轮可以发现路由。

> (15) As a possible congestion control mechanism in a subnet using virtual circuits internally,
a router could refrain from acknowledging a received packet until (1) it knows its last transmission along the virtual circuit was received successfully and (2) it has a free buffer. For simplicity, assume that the routers use a stop-and-wait protocol and that each virtual circuit has one buffer dedicated to it for each direction of traffic. If it takes T sec to transmit a packet (data or acknowledgement) and there are n routers on the path, what is the rate at which packets are delivered to the destination host? Assume that transmission errors are rare and that the host-router connection is infinitely fast.

Sol.

讲的是拥塞控制，这个方法不好，需要前一个包完全确认了才能传输下一个包。

The protocol is terrible. Let time be slotted in units of T sec. In slot 1 the source router sends the first packet. At the start of slot 2, the second router has received the packet but cannot acknowledge it yet. At the start of slot 3, the third router has received the packet, but it cannot acknowledge it either, so all the routers behind it are still hanging. The first acknowledgement can only be sent when the destination host takes the packet from the destination router. Now the acknowledgement begins propagating back. It takes two full transits of the network, 2(n − 1)T sec, before the source router can send the second packet. Thus, the throughput is one packet every 2(n − 1)T sec.

> (17) Describe two major differences between the ECN and the RED method.

Sol.

这两种是拥塞控制算法。

ECN (Explicit Congestion Notification, 显式拥塞通知)，路由器在它转发的数据包上打上标记，发出信号，接收方注意到拥塞时，发送应答包的同时告知发送方，让发送方降低传送速率。

RED (Random Early Detection, 随机早期检测), 路由器维护一个运行队列长度的平均值，当超过阈值的时候就开始随机丢弃数据包，快速发送方发现丢包就开始降低发送速率。

主要区别是一个显式通知，一个隐式通知，一个是缓存区开始不够了才通知，另一个是提前预知。

> (18) An ATM network uses a token bucket scheme for traffic shaping. A new token is put into the bucket every 5 μsec. Each token is good for one cell, which contains 48 bytes of data. What is the maximum sustainable data rate?

Sol.

With a token every 5 μsec, 200,000 cells/sec can be sent. Each packet holds 48 data bytes or 384 bits. The net data rate is then 76.8 Mbps.

> (19) A computer on a 6-Mbps network is regulated by a token bucket. The token bucket is filled at a rate of 1 Mbps. It is initially filled to capacity with 8 megabits. How long can the computer transmit at the full 6 Mbps?

Sol.

8 / (6 - 1) = 1.6s

> (21) The CPU in a router can process 2 million packets/sec. The load offered to it is 1.5 million packets/sec. If a route from source to destination contains 10 routers, how much time is spent being queued and serviced by the CPUs?

Sol.

这题用到排队论，服务速率为2，到达速率为1.5，那么服务时间为 1／2million / (1 - 1.5 / 2) = 2us。 10个routers就是20us.

> (22) Consider the user of differentiated services with expedited forwarding. Is there a guarantee that expedited packets experience a shorter delay than regular packets? Why or why not?

Sol.

不保证，过多包被标记为加速的，那么可能反而变慢了。

> (23) Suppose that host A is connected to a router R 1, R 1 is connected to another router, R 2, and R 2 is connected to host B. Suppose that a TCP message that contains 900 bytes of data and 20 bytes of TCP header is passed to the IP code at host A for delivery to B. Show the Total length, Identification, DF, MF, and Fragment offset fields of the IP header in each packet transmitted over the three links. Assume that link A-R1 can support a maximum frame size of 1024 bytes including a 14-byte frame header, link R1-R2 can support a maximum frame size of 512 bytes, including an 8-byte frame header, and link R2-B can support a maximum frame size of 512 bytes including a 12- byte frame header.

Sol.

```
Link A-R1 :
Length: 900 + 20 (TCP) + 20 (IP) = 940 Bytes, ID:X , DF:0 , MF:0 , Fragment offset:0
Link R1-R2:
(1) Length = 500; ID = x; DF = 0; MF = 1; Offset = 0 (2) Length = 460; ID = x; DF = 0; MF = 0; Offset = 60
Link R2-B:
(1) Length = 500; ID = x; DF = 0; MF = 1; Offset = 0 (2) Length = 460; ID = x; DF = 0; MF = 0; Offset = 60
不用去考虑数据链路层的成帧部分，IP协议不关心。
```

> (24) A router is blasting out IP packets whose total length (data plus header) is 1024 bytes. Assuming that packets live for 10 sec, what is the maximum line speed the router can operate at without danger of cycling through the IP datagram ID number space?

Sol.

IP包的ID字段拥有16位，因此65536个不同编号

65536 ＊ 1024 * 8 / 10 = 54 Gbps

> (25)  An IP datagram using the Strict source routing option has to be fragmented. Do you think the option is copied into each fragment, or is it sufficient to just put it in the first fragment? Explain your answer.

Sol.

Strict Source Routing 严格源路由，是IPv4头的可选项，表明该包必须经过指定路由。

必须要在每个fragment都要包括。

> (26) Suppose that instead of using 16 bits for the network part of a class B address originally, 20 bits had been used. How many class B networks would there have been?

Sol.

B类地址，前缀10定死，因此18bits可以用，所以有2^18个B类网络。

> (28) A network on the Internet has a subnet mask of 255.255.240.0. What is the maximum number of hosts it can handle?

Sol.

4096

> (29) 为什么以太网地址不能特定于一个网络，而IP地址却可以？

Sol.

Each Ethernet adapter sold in stores comes hardwired with an Ethernet (MAC) address in it. When burning the address into the card, the manufac- turer has no idea where in the world the card will be used, making the address useless for routing. In contrast, IP addresses are either assigned either stati- cally or dynamically by an ISP or company, which knows exactly how to get to the host getting the IP address.

> (30) A large number of consecutive IP address are available starting at 198.16.0.0. Suppose that four organizations, A, B, C, and D, request 4000, 2000, 4000, and 8000 addresses, respectively, and in that order. For each of these, give the first IP address assigned, the last IP address assigned, and the mask in the w.x.y.z/s notation.

Sol. 
```
A: 198.16.0.0 – 198.16.15.255 written as 198.16.0.0/20 
B: 198.16.16.0 – 198.23.15.255 written as 198.16.16.0/21 
C: 198.16.32.0 – 198.47.15.255 written as 198.16.32.0/20 
D: 198.16.64.0 – 198.95.15.255 written as 198.16.64.0/19
```

> (31) A router has just received the following new IP addresses: 57.6.96.0/21, 57.6.104.0/21, 57.6.112.0/21, and 57.6.120.0/21. If all of them use the same outgoing line, can they be aggregated? If so, to what? If not, why not?

Sol.

可以聚合成57.6.96.0/19, 这个时候会有57.6.120.0/21没有被聚合，但是有最大匹配原则所以不要紧。

> (32)  The set of IP addresses from 29.18.0.0 to 19.18.128.255 has been aggregated to 29.18.0.0/17. However, there is a gap of 1024 unassigned addresses from 29.18.60.0 to 29.18.63.255 that are now suddenly assigned to a host using a different outgoing
line. Is it now necessary to split up the aggregate address into its constituent blocks, add the new block to the table, and then see if any reaggregation is possible? If not, what can be done instead?

Sol.

不需要，因为有最长匹配，所以单独聚合即可。

> (34) Many companies have a policy of having two (or more) routers connecting the company to the Internet to provide some redundancy in case one of them goes down. Is this policy still possible with NAT? Explain your answer.

Sol.

After NAT is installed, it is crucial that all the packets pertaining to a single connection pass in and out of the company via the same router, since that is where the mapping is kept. If each router has its own IP address and all traffic belonging to a given connection can be sent to the same router, the mapping can be done correctly and multihoming with NAT can be made to work.

所以是可以的，只要网络中的每个主机发给特定router即可。

> (36) Describe a way to reassemble IP fragments at the destination.

Sol.

In the general case, the problem is nontrivial. Fragments may arrive out of order and some may be missing. On a retransmission, the datagram may be fragmented in different-sized chunks. Furthermore, the total size is not known until the last fragment arrives. Probably the only way to handle reassembly is to buffer all the pieces until the last fragment arrives and the size is known. Then build a buffer of the right size, and put the fragments into the buffer, maintaining a bit map with 1 bit per 8 bytes to keep track of which bytes are present in the buffer. When all the bits in the bit map are 1, the datagram is complete.

所以就是等尾巴来，就可以确定总长度，然后等所有分段都来就可以重组了。

> (37) Most IP datagram reassembly algorithms have a timer to avoid having a lost fragment tie up reassembly buffers forever. Suppose that a datagram is fragmented into four fragments. The first three fragments arrive, but the last one is delayed. Eventually, the timer goes off and the three fragments in the receiver's memory are discarded. A little later, the last fragment stumbles in. What should be done with it?

Sol.

前三段已经被discard了，那么第四段再来会被当成新的，过一段时间一样被扔。

> (38)  In both IP and ATM, the checksum covers only the header and not the data. Why do you suppose this design was chosen?

Sol.

其他部分的checksum可以交给上层协议，而且开销太大，此外头的错误非常严重。

> (39)  A person who lives in Boston travels to Minneapolis, taking her portable computer with her. To her surprise, the LAN at her destination in Minneapolis is a wireless IP LAN, so she does not have to plug in. Is it still necessary to go through the entire business with home agents and foreign agents to make e-mail and other traffic arrive correctly?

Sol.

当然需要，无线网是数据链路层和物理层的事情，和IP层无关，还是要利用家乡代理。

> (41) The Protocol field used in the IPv4 header is not present in the fixed IPv6 header. Why not?

Sol.

因为在IPv6头中有一个字段叫下一个头，会说明该字段要交给哪一种上层协议处理。

> (42) When the IPv6 protocol is introduced, does the ARP protocol have to be changed? If so, are the changes conceptual or technical?

Sol.

不需要做任何改变，只是IP地址需要更长的空间而已。

## 第六章：传输层

> (1) In our example transport primitives of Fig.6-2, LISTEN is a blocking call. Is this strictly necessary? If not, explain how a nonblocking primitive could be used. What advantage would this have over the scheme described in the text?

Sol.

The LISTEN call could indicate a willingness to establish new connections but not block. When an attempt to connect was made, the caller could be given a signal. It would then execute, say, OK or REJECT to accept or reject the con- nection. In our original scheme, this flexibility is lacking.

> (2) 像bittorrent这种点对点应用如何区分哪个是connect哪个listen呢？

Sol.

可以竞争，也可以随机，也可以由上层协议控制。

> (3) 假设网络层是百分百正确的那么三次握手协议会有什么样的变化？

Sol.

第三次握手就没有必要了，主机2收到主机1的请求时，直接就确立连接，返回给1确认信息后，1也确立连接。

> (7) Suppose that the clock-driven scheme for generating initial sequence numbers isused with a 15-bit wide clock counter. The clock ticks once every 100 msec, and the maximum packet lifetime is 60 sec. How often need resynchronization take place
a. (a)in the worst case?
b. (b)when the data consumes 240 sequence numbers/min?

Sol.

首先，现在的规定是这样的，通信的时候初始段的序号等于始终序号，因此在x秒的时候同步的信号序号为x，注意，建立连接之后，序号就和时间无关了！！！！

第二，在时间x时，不允许发送x+T(T是lifetime)序号的段，这是为什么呢？因为你这个段会生存T秒，而在随后的T秒内如果要建立一个连接，可能会和你现在发的这个段序号一样的懂吗？

(a) 题目是这样的，假设在70秒的时候，你建立了一个连接，这个时候你发出去的信号序号为70。注意，之后你要发送的序号是71，然后你一直忍着不发（这就是最坏的情况），然后时间绕了一圈回来了，时间变成了11秒，此时你再想发71，对不起，规则不允许，因为在11秒的时候，不允许发71。这就是最坏情况了，所以必须重新同步一次。答案是3276.8-60=3216.8

(b) 这个时候变成追及问题，最后计算出来结果是5361.3

总结，你发送速率与时钟速率越接近，需要同步的间隔就越长。但是如果一上来超过时钟速率就直接GG。

因为这种方法不好所以后来创造了三次握手，两边的初始序列号都是随机值，就不需要那么麻烦的时钟什么的啦。只要双方确认了，后面都好办。

> (9) Imagine that a two-way handshake rather than a three-way handshake were used to
set up connections. In other words, the third message was not required. Are deadlocks
now possible? Give an example or show that none exist.

Sol.

有可能出问题，主机2的确认被延迟了，那么可能会建立起一个重复的连接。

> (11) Consider the problem of recovering from host crashes (i.e.,Fig.6-18). If the interval
between writing and sending an acknowledgement, or vice versa, can be made relatively small, what are the two best sender-receiver strategies for minimizing the chance of a protocol failure?

Sol.

If the AW or WA time is small, the events AC(W) and WC(A) are unlikely events. The sender should retransmit in state S1; the receiver’s order does not matter.

> (13) Discuss the advantages and disadvantages of credits versus sliding window protocols.

Sol.

滑动窗口协议用于流量控制。

The sliding window is simpler, having only one set of parameters (the win- dow edges) to manage. Furthermore, the problem of a window being increased and then decreased, with the segments arriving in the wrong order, does not occur. However, the credit scheme is more flexible, allowing a dynamic management of the buffering, separate from the acknowledgements.

> (14) 拥塞控制的公平性方面的策略，加法递增乘法递减(AIMD).

> (15) Why does UDP exist? Would it not have been enough to just let user processes send
raw IP packets?

Sol.

无法确认端口。

> (17)  A client sends a 128-byte request to a server located 100 km away over a 1-gigabit optical fiber. What is the efficiency of the line during the remote procedure call?

Sol.

发送包需要`128*8/1G＝1.024us`, 在路上的时间100km/200000 = 50us, 然后一来一回100us，因此利用率约为1%。
所以说这个时候的制约不是带宽而是距离。

> (19) Both UDP and TCP use port numbers to identify the destination entity when delivering a message. Give two reasons for why these protocols invented a new abstract ID (port numbers), instead of using process IDs, which already existed when these protocols were designed.

Sol. 

进程id动态变化，不易管理，端口号可以被进程绑定，而且一些知名服务需要用固定端口号。

Here are three reasons. First, process IDs are OS-specific. Using process IDs would have made these protocols OS-dependent. Second, a single process may establish multiple channels of communications. A single process ID (per process) as the destination identifier cannot be used to distinguish between these channels. Third, having processes listen on well-known ports is easy, but well-known process IDs are impossible.

> (20) 何时选用基于UDP的RPC，合适使用基于TCP的RPC。

Sol.

RPC是远程过程调用，可以建立客户端－服务器应用。如果请求不是幂等的(幂等是如同计数器加一这种命令，执行次数不同会产生不同结果)，那就可以考虑使用UDP。同时，如果传递的数据包并不大，可以考虑使用UDP。

> (22) 最小TCP MTU的总长度是多少？包括TCP和IP的开销，但是不包括数据链路层的开销。

Sol.

MTU是最大传输单元，最小的TCP MTU可以设置，如果一台主机不设置的话默认是536+20=556字节的TCP段。Internet要求每台主机至少能够处理556字节的段。

> (23) RTP is used to transmit CD-quality audio, which makes a pair of 16-bit samples 44,100 times/sec, one sample for each of the stereo channels. How many packets per second must RTP transmit?

Sol.

Each sample occupies 4 bytes. This gives a total of 256 samples per packet. There are 44,100 samples/sec, so with 256 samples/packet, it takes 44100/256 or 172 packets to transmit one second’s worth of music.

按照标准答案的理解，RTP的一个packet只能传输1024字节，不清楚这个规定是在哪里。

> (25) Would it be possible to place the RTP code in the operating system kernel, along with the UDP code? Explain your answer.

Sol.

应该要分开，RTP是基于UDP的协议，其他应用程序也要调用UDP，因此最好可以把两段代码分开。

错了！

Sure. The caller would have to provide all the needed information, but there is no reason RTP could not be in the kernel, just as UDP is.

> (26) 主机1的端口p和主机2的端口q之间可能存在多个TCP连接吗？

Sol.

不可能，一对端口之间只能有一个TCP连接。一个进程可以有多个TCP连接。

> (27) ACK标志位有什么用？

Sol.

ACK标志位用来表示ACK字段是否有意义。其实在连接已经建立起来之后ACK标志位已经没有意义了，因为ACK是必须的。而在连接建立的过程中，这是非常重要的。

> (28) 为什么TCP段的有效载荷是65495字节？

Sol.

因为TCP长度为16位标示，所以最多65535字节，然后去掉TCP头20字节，去掉IP头20字节。剩下65495字节。

> (30) Consider the effect of using slow start on a line with a 10-msec round-trip time and no congestion. The receive window is 24 KB and the maximum segment size is 2 KB. How
long does it take before the first full window can be sent?

Sol.

慢速启动是TCP协议中拥塞控制的一个算法，略看。The first bursts contain 2K, 4K, 8K, and 16K bytes, respectively. The next one is 24 KB and occurs after 40 msec.

> (31) Suppose that the TCP congestion window is set to 18 KB and a timeout occurs. How big
will the window be if the next four transmission bursts are all successful? Assume that
the maximum segment size is 1 KB.

Sol.

也是拥塞控制的算法，TCP维护一个拥塞窗口，略看。The next transmission will be 1 maximum segment size. Then 2, 4, and 8. So after four successes, it will be 8 KB.

> (33) A TCP machine is sending full windows of 65,535 bytes over a 1-Gbps channel that has
a 10-msec one-way delay. What is the maximum throughput achievable? What is the line efficiency?

Sol.

One window can be sent every 20 msec. This gives 50 windows/sec, for a maximum data rate of about 3.3 million bytes/sec. The line efficiency is then 26.4 Mbps/1000 Mbps or 2.6 percent.

因此有延迟的网络传输效率和窗口大小，延迟有很大关系。

> (34) What is the fastest line speed at which a host can blast out 1500-byte TCP payloads with a 120-sec maximum packet lifetime without having the sequence numbers wrap around? Take TCP, IP, and Ethernet overhead into consideration. Assume that Ethernet frames may be sent continuously.

Sol.

TCP中每个Byte会占用一个序号，而TCP的sequence number是32位的，所以可以每120s发送2^32Bytes信息，然而1500B信息需要1500+20+20+26(以太网)的段帧来发送因此需要的带宽为`2 ^ 32 * 8 * 1566 / 1500 / 120 = 299Mbps`。

The goal is to send 2^32 bytes in 120 sec or 35,791,394 payload bytes/sec. This is 23,860 1500-byte frames/sec. The TCP overhead is 20 bytes. The IP overhead is 20 bytes. The Ethernet overhead is 26 bytes. This means that for 1500 bytes of payload, 1566 bytes must be sent. If we are to send 23,860 frames of 1566 bytes every second, we need a line of 299 Mbps. With any- thing faster than this we run the risk of two different TCP segments having the same sequence number at the same time.

> (35) 为什么那么多人在为了ipv4的局限性做努力，而对TCP的局限性却没有人这样做。

Sol.

根本原因是IP协议运行在所有路由器上。

IP is a network level protocol while TCP is an end-to-end transport level protocol. Any change in the protocol specification of IP must be incorporated on all routers in the Internet. On the other hand, TCP can works fine as long as the two end points are running compatible versions. Thus, it is possible to have many different versions of TCP running at the same time on different hosts, but not this is not the case with IP.

> (36)  In a network that has a maximum TPDU size of 128 bytes, a maximum TPDU lifetime of 30 sec, and an 8-bit sequence number, what is the maximum data rate per connection?

Sol.

TPDU:Transport Protocol Data Unit 协议数据单元。

`2 ^ 8 * 128 * 8 / 30 = 8.7kbps`

> (37) Suppose that you are measuring the time to receive a TPDU. When an interrupt occurs,
you read out the system clock in milliseconds. When the TPDU is fully processed, you read out the clock again. You measure 0 msec 270,000 times and 1 msec 730,000 times. How long does it take to receive a TPDU?

Sol.

27次是0ms，73次是1ms，那么平均是730us。

> (38) A CPU executes instructions at the rate of 1000 MIPS. Data can be copied 64 bits at a time, with each word copied costing 10 instructions. If an coming packet has to be copied four times, can this system handle a 1-Gbps line? For simplicity, assume that all instructions, even those instructions that read or write memory, run at the full 1000- MIPS rate.

Sol.

`1000M * 64 /10 / 4 = 1.6 Gbps > 1Gbps 可以`.

> (41) For a 1-Gbps network operating over 4000 km, the delay is the limiting factor, not the bandwidth. Consider a MAN with the average source and destination 20 km apart. At what data rate does the round-trip delay due to the speed of light equal the transmission delay for a 1-KB packet?

Sol.

`20 * 2 / 200000 = 200us延迟
发送1KB要200us的话，带宽至少要1024 * 8 * 1 / 200u = 40 Mbps`

> (43) What is the bandwidth-delay product for a 50-Mbps channel on a geostationary satellite? If the packets are all 1500 bytes (including overhead), how big should the window be in packets?

Sol.

The round-trip delay is about 540 msec, so with a 50-Mbps channel the bandwidth-product delay is 27 megabits or 3,375,000 bytes. With packets of 1500 bytes, it takes 2250 packets to fill the pipe, so the window should be at least 2250 packets.
