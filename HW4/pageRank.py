import networkx as nx
G = nx.read_edgelist("edgeList.txt", create_using=nx.DiGraph())
myPR = nx.pagerank(G, alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None, weight='weight',dangling=None) 
write1 = 'external_pageRankFile.txt'
f = open(write1, mode ='w')
for id in myPR:
	#t = id.split('.')
	temp = "/Users/lidunxuan/Desktop/CS 572/HW4/solr-7.7.3/latimes/"+id+ "="+ str(myPR[id]) + "\n"
	f.write(temp)
f.close()
