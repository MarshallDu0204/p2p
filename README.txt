(1) About the file, the p2pClient is the main java file which contain main function. So to start it, You can just type "javac p2pClient.java"
to compile the code, then type "java p2pClient" to start it. After the client initiation complete, it will display that it is ready for user
input. Then you can type the command specify in the file.

(2) About the query ID I pick each host name and multiplied them with 100 to generate a sufficient query ID range for each peer to prevent query
ID collision. So for example for host "eecslab-11.case.edu" I pick the number 11 multiple 100 as 1100. So the range of host "eecslab-11.case.edu"
is 1100 to 1199

(3) About the query method. The first technique I prevent the multiple same query is that create a list in each client and if the queryID has been
exist in the list, the function will only return a empty message without flooding it to the further server. The second technique I use is let the query
flooding method act as a depth first search. So for each client, when the query message is flooding through to each neighbors, it will first wait the
answer of the first neighbour then flood to the next neighbour.

