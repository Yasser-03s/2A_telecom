import scipy.io
import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression as linreg

data1= np.load('./data/data1.npy')
data2= np.load('./data/data2.npy')
data3= np.load('./data/data3.npy')
end4= scipy.io.loadmat('./data/end4_groundTruth.mat')
Urban= scipy.io.loadmat('./data/Urban_R162.mat')


def linreg2D(data,titre):
    #extraction des colonnes de data
    x= data[0]
    y= data[1]
    x_reshape= x[:, None]

    reg= linreg().fit(x_reshape,y)    #ajuster le model: chercher beta0 et beta1 qui fit le mieux 
    y_pred= reg.predict(x_reshape)     #predire le y

    plt.figure()
    plt.scatter(x,y) #plt.scatter pout nuage de points
    plt.plot(x, y_pred, color='red',label= 'linear regression')
    plt.title("nuage de "+titre)
    plt.xlabel('x')
    plt.ylabel('y')
    plt.legend()
    plt.show()
    
    erreur_apprentissage= (1/len(y))*sum((y_pred-y)**2)
    print("erreur d'apprentissage" + titre +": "+ str(erreur_apprentissage))
    
linreg2D(data1, 'data1')
linreg2D(data2, 'data2')

## ----------------------- version OLM implémenté---------------------------
# #extraction des colonnes de data1
# x1= data1[0]
# y1= data1[1]
# X = np.column_stack((np.ones_like(x1), x1))

# inverse= np.linalg.inv(X.T @ X)
# beta = inverse @ X.T @ y1 

# y_pred = X @ beta   #predire le y

# plt.figure()
# plt.scatter(x1,y1) #plt.scatter pout nuage de points
# plt.plot(x1, y_pred, color='yellow',label= 'linear regression')
# plt.title("nuage de points data1")
# plt.xlabel('x')
# plt.ylabel('y')
# plt.legend()
# plt.show()

# erreur_apprentissage= (1/len(y1))*sum((y_pred-y1)**2)

# print("erreur d'apprentissage data1: "+ str(erreur_apprentissage))
## ---------------------------------------------------------------------------


def regression(data, phi, beta):
    x= data[0]
    y= data[1]
    Jbeta= np.linalg.norm( y - phi @ beta)**2
    
    