import csv

from sklearn import preprocessing
from sklearn.neighbors import KNeighborsClassifier

gx = []
gy = []
gz = []
gnet = []
label = []

print("Scanning Dataset")

# Reading csv database
with open('Dataset/data.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    line_count = 0
    for row in csv_reader:
        gx.append(float(row[0]))
        gy.append(float(row[1]))
        gz.append(float(row[2]))
        gnet.append(float(row[3]))
        label.append(int(row[4]))
    print("Dataset Scanning Completed")

data = []

for i in range(0, len(gx)):
    data.append([gx[i], gy[i], gz[i], gnet[i], label[i]])


def split_data(training_samples_percentage, data):
    size = len(data)
    counter = int(float(training_samples_percentage / 100) * size)
    training_data = []
    testing_data = []

    for row in data:
        if counter >= 0:
            training_data.append(row)
            counter -= 1
        else:
            testing_data.append(row)

    return training_data, testing_data


print("\n===========Splitting Dataset=========\n")

train, test = split_data(70, data)

print("Size of Total Dataset: ", len(data))
print("Size of Training set: ", len(train))
print("Size of Testing set: ", len(test))

print("\n===========Splitting complete=========\n")

gx = []
gy = []
gz = []
gnet = []
label = []

for row in train:
    gx.append(row[0])
    gy.append(row[1])
    gz.append(row[2])
    gnet.append(row[3])
    label.append(row[4])

# Creating labelEncoder
le = preprocessing.LabelEncoder()

# Combining features into single list of tuples
features = list(zip(gx, gy, gz, gnet))

# Encoding labels
labels = le.fit_transform(label)

# Creating model
model = KNeighborsClassifier(n_neighbors=46)

# Train the model using the training sets
model.fit(features, label)


print("\n===========Testing Model=========\n")

attempt = 0
correct = 0

for point in test:
    y = model.predict([point[0:4]])[0]
    yb = point[4]
    attempt += 1
    if y == yb:
        correct += 1

print("Accuracy with test data = ", float(correct/attempt)*100)

attempt = 0
correct = 0

for point in train:
    y = model.predict([point[0:4]])[0]
    yb = point[4]
    attempt += 1
    if y == yb:
        correct += 1

print("Accuracy with training data = ", float(correct/attempt)*100)

attempt = 0
correct = 0

for point in data:
    y = model.predict([point[0:4]])[0]
    yb = point[4]
    attempt += 1
    if y == yb:
        correct += 1

print("Accuracy with total data = ", float(correct/attempt)*100)

print("\n===========Analyzing Results=========\n")

green = []
yellow = []
red = []

for point in data:
    if point[4] == 0:
        green.append(point)
    elif point[4] == 1:
        yellow.append(point)
    else:
        red.append(point)


def zonal_analysis(database):

    attempt = 0
    correct = 0
    rp = 0
    gp = 0
    yp = 0

    for point in database:
        y = model.predict([point[0:4]])[0]
        yb = point[4]
        attempt += 1
        if y == yb:
            correct += 1
        if y == 0:
            gp +=1
        elif y == 1:
            yp += 1
        else:
            rp += 1

    print("Total points : ", attempt)
    print("Predicted Green :", float(gp/attempt)*100)
    print("Predicted Yellow :", float(yp/attempt)*100)
    print("Predicted Red :", float(rp/attempt)*100)
    print("Overall accuracy = ", float(correct/attempt)*100)


print("\n==========Green Zone Points==========\n")
zonal_analysis(green)
print("\n==========Yellow Zone Points==========\n")
zonal_analysis(yellow)
print("\n==========Red Zone Points==========\n")
zonal_analysis(red)