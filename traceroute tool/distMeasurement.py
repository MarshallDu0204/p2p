import socket
import struct
import time


def readTarget(path):
	with open(path) as f:
		info = f.readlines()
		return info

def ipMatch(senderIP,recvPacket):
	match = 1
	senderIP = senderIP.split(".")
	recvAddr = []
	for i in range(12,16):
		recvAddr.append(ord(recvPacket[i:i+1]))
	for i in range(0,4):
		recvAddr[i] = str(recvAddr[i])
	for i in range(0,4):
		if senderIP[i]!=recvAddr[i]:
			match = 0
			break

	if recvPacket[20]!=3 or recvPacket[21] != 3:
		match = 0

	return match

def destPortMatch(destPort,recvPacket):
	packetPort = struct.unpack("!H",recvPacket[50:52])
	if destPort == packetPort[0] and recvPacket[20]==3 and recvPacket[21] == 3:
		return 1
	else:
		return 0

def checkPayLoad(recvPacket):
	if len(recvPacket)>56:
		payloadLength = len(recvPacket)-56
		return payloadLength
	else:
		return 0


def measurement(hostName,serverPort,timewait,packetTtl):

	hostAddr = 	socket.gethostbyname(hostName)

	pingAddr = (hostAddr,serverPort)

	bindAddr = ("",0)

	message = "measurement for class project. questions to professor mxr136@case.edu"

	payload = bytes(message+'a'*(1472-len(message)),'ascii')

	udpSocket = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)

	udpSocket.setsockopt(socket.IPPROTO_IP,socket.IP_TTL,packetTtl)

	udpSocket.sendto(payload,pingAddr)

	sendTime = time.time()

	icmpSocket = socket.socket(socket.AF_INET,socket.SOCK_RAW,socket.IPPROTO_ICMP)

	timeout = struct.pack("ll", timewait, 0)

	icmpSocket.setsockopt(socket.SOL_SOCKET, socket.SO_RCVTIMEO, timeout)

	icmpSocket.bind(bindAddr)

	try:
		icmp_packet,recvAddr = icmpSocket.recvfrom(1600)

	except:

		return [-1]

	recvTime = time.time()

	rttTime = recvTime-sendTime

	actTTL = icmp_packet[36]

	hostNum = packetTtl-actTTL

	udpSocket.close()

	icmpSocket.close()

	result0 = ipMatch(hostAddr,icmp_packet)

	result1 = destPortMatch(serverPort,icmp_packet)

	payloadLength = checkPayLoad(icmp_packet)

	return [hostName,hostAddr,hostNum,rttTime,result0,result1,payloadLength]

def showResult(result):
	solu = ""
	solu = "Hostname: "+result[0]+" |"
	solu = solu+"Ip address: "+result[1]+" |"
	solu = solu+"Number of host: "+str(result[2])+" |"
	solu = solu+"rtt time is: "+str(result[3])+" |"
	ipResult = "Not match"
	if result[4]==1:
		ipResult = "Match"
	solu = solu+"ip match result: "+ipResult+" |"
	portResult = "Not match"
	if result[5]==1:
		portResult = "Match"
	solu = solu+"port match result: "+portResult+" |"
	solu = solu+"reply payload length: "+str(result[6])
	print(solu)
	print("-----------------")


def main():
	packetTtl = 255
	serverPort = 33434
	timewait = 3

	addrList = readTarget("target.txt")
	for host in addrList:
		host = host.strip()
		result = measurement(host,serverPort,timewait,packetTtl)
		if len(result)==1:
			print(host," server doesn't reply")
		else:
			showResult(result)

if __name__ == '__main__':
	main()
