import csv
if __name__=="__main__":
    with open('fetch_latimes.csv') as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        count_success = 0
        count_failure = 0
        count_total_fetches = 0
        for row in csv_reader:
            if row[0] == "URL": continue
            count_total_fetches+=1
            if row[1].startswith("2"):
                count_success += 1
            else:
                count_failure += 1

        print("fetches success, fetches failed")
        print(count_success, count_failure)
        print("************************************")
        print("Total fetches attempted")
        print(count_total_fetches)
        print("***************************")
    d = {}
    with open('urls_latimes.csv') as csv_file:
        totalURL = 0
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
            if row[0] == "URL": continue
            totalURL += 1
            if row[0] not in d:
                d[row[0]] = row[1]
    print("total URL = ")
    print(totalURL)
    print("total unique URLS extracted")
    print(len(d))
    print("************************")
    count_in = 0
    count_out = 0
    for value in d.values():
        if value == "OK":
            count_in+=1
        else:
            count_out+=1
    print("Unique within, unique outside")
    print(count_in, count_out)
    print("*********************************")
    dct = {}
    with open('fetch_latimes.csv') as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
               if row[0] == "URL": continue
               if row[1] not in dct:
                   dct[row[1]] = 1
               else:
                   dct[row[1]] += 1
    print("status code counts")
    for key, value in dct.items():
        print(key, value)
    print("******************************")
    dt = {}
    with open('visit_latimes.csv') as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
            if row[0] == "URL": continue
            if row[3] not in dt:
                dt[row[3]] = 1
            else:
                dt[row[3]] += 1
        print("Image types and counts")
        for key, value in dt.items():
            print(key, value)
        print("**************************")
    lessthanKB = 0
    onetotenKB = 0
    tentoHund = 0
    hundtoMb = 0
    greaterMB = 0
    with open('visit_latimes.csv') as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
            if row[0] == "URL": continue
            if int(row[1].split(' ')[0]) < 1024:
                lessthanKB += 1
            elif int(row[1].split(' ')[0]) < 1024*10:
                onetotenKB += 1
            elif int(row[1].split(' ')[0]) < 1024*100:
                tentoHund += 1
            elif int(row[1].split(' ')[0]) < 1024*1024:
                hundtoMb += 1
            else:
                greaterMB += 1

    print("File size counts")
    print(lessthanKB, onetotenKB, tentoHund, hundtoMb, greaterMB)
    print("************************************")

    sum = 0
    with open('visit_latimes.csv') as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
            if row[0] == "URL": continue
            sum += int(row[2])
        print("Total URLS extracted: ")
        print(sum)
        print("**************************")

