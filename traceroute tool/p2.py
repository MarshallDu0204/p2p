import socket
import struct
import time


def readTarget(path):
	with open(path) as f:
		info = f.readlines()
		return info


def pingHost(hostName):

	hostAddr = 	socket.gethostbyname(hostName)

	pingAddr = (hostAddr,33434)

	bindAddr = ("",0)

	message = "measurement for class project. questions to professor mxr136@case.edu"

	payload = bytes(message+'a'*(1472-len(message)),'ascii')

	udpSocket = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)

	udpSocket.setsockopt(socket.SOL_SOCKET,socket.IP_TTL,50)

	udpSocket.sendto(payload,pingAddr)

	sendTime = time.time()

	print("message send to: ",hostAddr)

	icmpSocket = socket.socket(socket.AF_INET,socket.SOCK_RAW,socket.IPPROTO_ICMP)

	timeout = struct.pack("ll", 5000, 0)

	icmpSocket.setsockopt(socket.SOL_SOCKET, socket.SO_RCVTIMEO, timeout)

	icmpSocket.bind(bindAddr)

	print("recvSocket is ready!")

	icmp_packet,recvAddr = icmpSocket.recvfrom(1600)

	recvTime = time.time()

	rttTime = recvTime-sendTime

	print(icmp_packet)

	print("RTT is ",rttTime)

	udpSocket.close()

	icmpSocket.close()

	print(recvAddr,len(icmp_packet))

	info = struct.unpack("!H",icmp_packet[10:12])[0]

	print(info)

	'''

	icmp_packet = struct.unpack("ii",icmp_packet)

	print(icmp_packet)

	icmp_packet.decode('ascii')

	print(icmp_packet)
	'''

pingHost("www.google.com")


