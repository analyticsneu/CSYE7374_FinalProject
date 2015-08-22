from pyspark import SparkContext
from pyspark.mllib.regression import LabeledPoint 
from pyspark.mllib.classification import SVMWithSGD 
import numpy as np

sc = SparkContext()

## The dataset contains two months of weather data
lines = sc.textFile('/Users/shwetaanchan/Desktop/Final Project/finalData.csv')

# Taking the header row from the file
header = lines.take(1)[0]

#Removing the header from the RDD
rows = lines.filter(lambda line: line!=header)

#Removing rows with N/As and empty values
data = rows.filter(lambda line: line != remove_na(line))

def remove_na(line):
    fields = [x for x in line.split(',')]
    if (fields[9]=='N/A') or (fields[6]=='') or (fields[8]=='') or (fields[4]=='VRB'):
        return line  
    else:
        pass
    
#creating a list with unique output values

weather_output = [" Clear sky (Night)"," Partly cloudy (Night)"," (White) Medium-level cloud"," (Black) Low-level cloud",
           " Drizzle"," Fog"," Hail shower (Night)"," Heavy Rain", " Heavy rain shower (Day)", 
           " Heavy rain shower (Night)"," Light rain"," Light rain shower (Day)", " Light rain shower (Night)",
           " Mist"," Sleet"," Sunny (Day)"," Sunny intervals"," Thunder storm"," Thundery shower (Day)", " Thundery shower (Night)", 
           " Hail shower (Day)", " Heavy snow", " Heavy snow shower (Day)"," Heavy snow shower (Night)",
           " Light snow", " Light snow shower (Day)", " Light snow shower (Night)"]
    
## Creating key-value pairs for categorical conversions
#
wind_direction = {"N":0,"NNE":22.5,"NE":45.0,"ENE":67.5,"E":90.0,"ESE":112.5,"SE":135.0,"SSE":157.5,"S":180.0,"SSW":202.5,
             "SW":225.0,"WSW":247.5,"W":270.0,"WNW":292.5, "NW":315.0,"NNW":337.5}

time_list = {"0:00":1.0, "1:00":2.0, "2:00":3.0, "3:00":4.0, "4:00":5.0, "5:00":6.0, "6:00":7.0, "7:00":8.0,
             "8:00":9.0, "9:00":10.0, "10:00":11.0, "11:00":12.0, "12:00":13.0, "13:00":14.0, "14:00":15.0,
             "15:00":16.0, "16:00":17.0, "17:00":18.0, "18:00":19.0, "19:00":20.0, "20:00":21.0, "21:00":22.0, 
             "22:00":23.0, "23:00":24.0}


## This function converts the categorical to numerical values
#
def mappingfunction(line):
    values = [x for x in line.split(',')]
    month= values[2][:1]
    date = values[2][2:4]
    
    if date[1:2] == '/':
        date = date[:1]# This is to remove the  '/'as for the first ten values dates will be a single digit number
        values[3] = date
    else:
        values[3] = date
        
    values[2] = month

    values[4] = wind_direction[values[4]]
    new_str = str(values[4])
    
    values[1] = time_list[values[1]]
    new_time = str(values[1])
    
    #values[9] = weather_output[values[9]]
    
    up_list = [values[0]] + [new_time] + values[2:4] + [new_str] + values[5:10]
    return ', '.join(up_list)


## Updated data with categorical values
#
updated_data = data.map(lambda line: mappingfunction(line))

def parse_Data(line,output):
    values = [x for x in line.split(',')]
    just_numbers = list(values[0:9])
    key_feature = 0.0  
    if values[9] == output:
        key_feature = 1.0
    return LabeledPoint(key_feature, [float(x) for x in just_numbers])

list_weights = []    

for output in weather_output: 
    lpdata = updated_data.map(lambda line: parse_Data(line,output))
    
    training, test = lpdata.randomSplit([0.7,0.3])
    
    model = SVMWithSGD.train(training, iterations=100)
    
    list_weights.append(model.weights)
    
    labelsAndPreds = test.map(lambda p: (p.label, float(model.predict(p.features))))
    
    testErr = labelsAndPreds.filter(lambda (v, p): v != p).count() / float(test.count())
    
    print('Test Error = ' + str(testErr))

    
test_list = [3321.0,16.0,7.0,30.0,315.0,10.0,45000.0,15.0,1021.0]
print ("Next we have: ")

for weights in list_weights:
    weightedValue = np.dot(np.transpose(test_list),weights)
    print("Weighted Value: "+ str(weightedValue))