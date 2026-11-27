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
    print("erreur d'apprentissage OLS" + titre +": "+ str(erreur_apprentissage))
    
# linreg2D(data1, 'data1')
# linreg2D(data2, 'data2')

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


def phi(x,q):
    return np.array([[x[k]**j for j in range(q+1)] for k in range(len(x)) ])

# nu=[]
# sigma=[]

# def phi2(x,i):
#     return (1+np.exp(-(x-nu[i])/sigma[i]))**-1

def trier(x, y_pred):
    idx = np.argsort(x)          # indices qui trient x par ordre croissant
    return x[idx], y_pred[idx]   # même ordre appliqué aux deux tableaux

def regression(data, phi,q,titre):
    #entrée: les données et la fonction phi, l'ordre q et le titre de la dataset
    #sortie: la liste des paramètre beta et affiche la courbe d'apprentissage et l'erreur d'apprentissage
    x= data[0]
    y= data[1]
    
    X = phi(x,q)
    print("rang phi: "+str(np.linalg.matrix_rank(X)))
    if(np.linalg.matrix_rank(X)!=q+1):
        print("instable, rang(phi) =/= q+1 ("+str(q+1)+")")
        return 0
    inverse= np.linalg.inv(X.T @ X)
    beta = inverse @ X.T @ y 
    y_pred= X @ beta
    
    Jbeta = np.linalg.norm(y - y_pred)**2
    n = len(y)  # nb de lignes
    erreur= Jbeta/n
    print("erreur d'apprentissage de l'espace de redescription de " + titre + " d'ordre "+str(q)+": "+ str(erreur))
    
    
    plt.figure()
    plt.scatter(x,y) #plt.scatter pout nuage de points
    plt.scatter(x, y_pred, color='yellow',label= "scatter régression d'ordre "+str(q))
    #------------------- On ajoute une étape pour trier x et y_pred, sinon on a des zigzag--------------------------
    x_trie, y_pred_trie = trier(x, y_pred)
    plt.plot(x_trie, y_pred_trie, color='yellow', label="courbe régression d'ordre " + str(q))
    #---------------------------------------------------------------------------------------------------------------
    plt.title("nuage de points de "+titre)
    plt.xlabel('x')
    plt.ylabel('y')
    plt.legend()
    plt.show()
    
    
    
    return beta

# regression(data2, phi,2,"data2")
# regression(data3, phi,1,"data3")
# regression(data3, phi,2,"data3")
# regression(data3, phi,3,"data3")
# regression(data3, phi,5,"data3")
# regression(data3, phi,10,"data3")
# regression(data3, phi,15,"data3")


def ridgereg(data, titre):
    x=data[0]
    y=data[1]
    p=len(y)
    X=np.column_stack((np.ones_like(x), x))
    p= np.linalg.matrix_rank(X)