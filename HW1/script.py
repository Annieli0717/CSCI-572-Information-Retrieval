from bs4 import BeautifulSoup 
from time import sleep
import time
import requests
from random import randint
from html.parser import HTMLParser
import ssl
import urllib3
import json

urllib3.disable_warnings()
USER_AGENT = {'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36'}
class SearchEngine:
	@staticmethod
	def search(query, sleep=True):
		if sleep: # Prevents loading too many pages too soon
			time.sleep(randint(5, 10))
		temp_url = '+'.join(query.split()) #for adding + between words for the query
		url = 'https://www.duckduckgo.com/html/?q=' + temp_url
		soup = BeautifulSoup(requests.get(url, headers=USER_AGENT).text, "html.parser")
		new_results = SearchEngine.scrape_search_result(soup) 
		return new_results
	@staticmethod
	def scrape_search_result(soup):
		# f = open("results.txt", "x")
		# f.write(str(soup))
		raw_results = soup.find_all("a", attrs = {"class" : "result__a"}, href=True) # class: 1. "badge--ad" = ads 2. "result__a" = non-ads
		# f.write(str(raw_results))
		results = [] 
		# implement a check to get only 10 results and also check that URLs must not be duplicated
		for result in raw_results:
			link = result.get('href')
			
			if "duckduckgo.com" not in link:
				results.append(link)
			# f.write(link + '\n') 
		identical_results = list(dict.fromkeys(results))
		# f.close()
		return identical_results[:10]

#############Driver code############
queries = ["A two dollar bill from 1953 is worth what",
"What is franky jonas 's favorite color",
"Is there an emergency action plan",
"How much does medical insurance cost for a single person",
"What is noah cyrus address so you can send her a fan mail",
"Have many points did wayne getzky get",
"How wide can vagina 's get",
"Calories in a lollipop",
"Pdf password cracker v 3.1 registration key",
"Another name for karyokinesis",
"What continent you indonesia on",
"Do deers live in savanna",
"What did lice cause in ww1",
"What women invented the brush",
"What is the order of electromagnetic radiation decreasing frequency",
"New tropicana advert",
"Where did siddartha Gautama live and teach",
"2003 GMC yukon ignition",
"What is the value of 1928 us 50 bill",
"Where is alpha amylase released",
"Which Band did Krauss join",
"What is the use of fruits",
"Where do sugar gliders like to live",
"How many championships have lakers",
"What are china major rivers and deserts",
"What do dholes eat",
"Stereo wiring diagram for a 1991 Toyota Camry",
"Names of divine horses on howrse",
"What is the population of teachers in the US",
"How did Fidel V Ramos campaigned",
"What are this 13 systems of human body",
"What are the Flickertail 's eating habits",
"What kind of money does croatia have",
"What do our circulatory system includes",
"A vaccination produces what kind of immunity",
"Where does Justin Timbelake live",
"Is corllins university a credited legal college",
"What is a myocaridal infaraction",
"When did bobsledding first enter",
"500 mg of cinnamon equal how many teaspoons",
"How many religions in spain",
"How many lice did bob marly have",
"What were the domsetic consequences of world war 1",
"What is the meaning of multitasking and multiuser operating system",
"The sun and the celestial bodies that orbit the sunincluding planets satellites asteroids comets dust and gas",
"Tell you about vampire bats",
"Specifying categories",
"How much does a rookie card of babe ruth cost",
"So what is fair trade",
"What is the secret ingredient of coke",
"When and how did Slobodan Milosevic die",
"What do teardrop tattoos on your eyes symbolize",
"What colors can a Yorkie be",
"On neopets how can you put a code in a scroll box without the code being in the same box as another",
"How much does it cost to rent a limo for the day in Madison WI",
"Where is the 50 story eiffel tower located",
"What is the length of an amtrak train",
"Where to give ideas for an invention",
"You are a big fan or shakira how you can send her a letter",
"Code to beanie babies",
"How much is s in roman numerals",
"What is the origin of the mineral silver",
"What is the principle behind the bluetooth",
"What is transmission spectrum",
"How many nutrons does radon have",
"What color is rihanna eyes",
"Who invented metal coil thermometer",
"How did Rome really begin",
"What part of california do corbin live in",
"What is the instrument called that is used to measure tornado 's",
"How does the future look on being a lawyer",
"How do you adjust brightness on your laptop",
"Whenwhere did william morris die",
"How do sanction help to keep the global community safe and secure",
"What is the function of command xargs in linux",
"How does air pollution affect the food webs",
"What was the name of the Prime Minister in Fiji in 2009",
"What kind of meaning do proverbs have",
"How many planet",
"What is filters harmful substances from lymph",
"How did park yong ha died",
"How did people make dresses in colonial times",
"How many liters are in a cup of milk",
"How much does marijuana coast per ounce",
"What is the purpose of a steak plate",
"What is the name of the four sperm cells",
"What is the longitudes of delhi",
"How is columbia related to venezuela",
"Fiona wood what is she famous for",
"What sounds does brakes make",
"What nationality are the people in germany",
"What niche do plants have",
"How many gold medals did mia hamm earn",
"What did Roger Bannister",
"How much does a medical abortion cost at three weeks",
"What is rank 2nd in populated city",
"What do mexicans houses look like",
"64 millilitres how many litres",
"You want to text dirty with your boyfriend what should you say",
"Can you give me example of anecdotes by the filipino writers"]

results = {}
for x in range(len(queries)):
	search_results = SearchEngine.search(queries[x]) 
	# results[queries[x]] = search_results
	results["Query" + str(x + 1)] = search_results

with open('hw1.json', 'w') as outfile:
    json.dump(results, outfile)
####################################
