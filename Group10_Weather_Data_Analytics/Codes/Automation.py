import urllib2
import mechanize


br = mechanize.Browser()
#br.set_all_readonly(False)    # allow everything to be written to
br.set_handle_robots(False)   # ignore robots
br.set_handle_refresh(False)  # can sometimes hang without this
#br.addheaders =                 # [('User-agent', 'Firefox')]



response = br.open("http://datagovuk.cloudapp.net/query")
#print response.read()      # the text of the page
response1 = br.response()  # get the response again
#print response1.read()     # can apply lxml.html.fromstring()

'''for form in br.forms():
    print "Form name:", form.name
    print form'''

br.form = list(br.forms())[0]

'''
Create the list which has tuples in each cell
'''    
input_list =  []
for date in range(1,32):
    for month in [3, 4, 5, 6, 7]:
        for year in [2015]:
            if date == 31 and month == 6:
                pass
            elif date == 31 and month == 4:
                pass
            else:
                if month < 10:
                    str_month = "0" + str(month)
                else:
                    str_month = str(month)
                if date < 10:
                    str_date = "0" + str(date)
                else:
                    str_date = str(date)
                date_value =  str_date +  "/" + str_month   + "/" + str(year)
            #time_value = []
            for i in range(24):
                if i < 10:
                    temp_value = "0" + str(i) + "00"
                    #time_value.append(temp_value)
                else:
                    temp_value = str(i) + "00"
                    #time_value.append(temp_value)
                input_list.append(('Observation',date_value, temp_value))
 
#print input_list
output_file = open("final_csv.csv", "w")
output_file.write('Site Code,Site Name,Latitude,Longitude,Region,Observation Time,Observation Date,Wind Direction,Wind Speed,Wind Gust,Visibility,Screen Temperature,Pressure,Pressure Tendency, Significant Weather')
output_file.write("\n")
for each_tuple in input_list:
    print "============================"
    print "each tuple is", each_tuple
    print "============================"
    response = br.open("http://datagovuk.cloudapp.net/query")
    response1 = br.response()  # get the response again
    br.form = list(br.forms())[0]
    for control in br.form.controls:
        if control.type == "select":
            if control.name == "Type":
                control.set_value_by_label([each_tuple[0]])
            elif control.name == "PredictionTime":
                control.value = [each_tuple[2]]
        elif control.type == "text":
            control.value = each_tuple[1]
    response = br.submit()
    for each_line in response.read().split():
        index = 1
        if 'csv' in each_line:
            csv_file = br.retrieve(each_line[1:-3])[0]
            csv_data = open(csv_file)
            for each_line in csv_data.readlines():
                if index != 1:
                    print each_line
                    output_file.write(each_line)
                index += 1
            break
    br.back()   # go back   '''
