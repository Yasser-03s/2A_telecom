import matplotlib.pyplot as plt

from matplotlib import cm
from matplotlib.colors import ListedColormap


#plt.ginput(2) function, which pauses execution until you click twice on the image and returns the (x, y)

import numpy as np
import scipy.signal
from random import randrange
import time; start_time = time.time()


A= ['BBBNNBN','BBNNBBN','BBNBBNN','BNNNNBN','BNBBBNN','BNNBBBN','BNBNNNN','BNNNBNN','BNNBNNN','BBBNBNN']
B= ['BNBBNNN','BNNBBNN','BBNNBNN','BNBBBBN','BBNNNBN','BNNNBBN','BBBBNBN','BBNBBBN','BBBNBBN','BBNBNNN']
C= ['NNNBBNB','NNBBNNB','NNBNNBB','NBBBBNB','NBNNNBB','NBBNNNB','NBNBBBB','NBBBNBB','NBBNBBB','NNNBNBB']


#------------ création d'une palette gray red pour verifier que l'échantillonage des pixels est bon-------------

gray_red= ListedColormap(np.vstack([cm.gray(np.linspace(0, 1, 256)), [1, 0, 0, 1]]))

#---------------------------------------------------------------------------------------------------------------

def bw(img):
    # MODIF : conversion en float avant la somme (sinon débordement uint8 pour un JPEG)
    bw= (img[:,:, 0].astype(float)+img[:,:, 1].astype(float)+img[:,:, 2].astype(float))/3;
    return bw

img= bw(plt.imread("./EAN13.jpg"));
img2= bw(plt.imread("./ean_13.png"));
img3= bw(plt.imread("./images.png"));



# img = mpimg.bw


def rayon_manuel(img):
    plt.imshow(img)
    print("Click 2 points on the image...")
    points = plt.ginput(2)

    plt.close() 
    return points

# MODIF : L peut être imposé de l'extérieur, et les indices sont bornés à l'image
def signature(x1,y1,x2,y2,img,L=None):
        if L is None:
            L= int(np.ceil(np.sqrt((x2-x1)**2+(y2-y1)**2)))
        
        t= np.linspace(0,1,L)
        xk= x1 + t * (x2 - x1)
        yk= y1 + t * (y2 - y1)
    
        #methode plus proche voisin: pixel=[ligne(y), colonne(x)]
        ik= np.clip(np.round(yk).astype(int), 0, img.shape[0]-1)
        jk= np.clip(np.round(xk).astype(int), 0, img.shape[1]-1)
        
        pixels_code=img[ik,jk]     #  l' extraction des pixels coupés par le rayon 
        return L, pixels_code

# MODIF : retourne aussi le profil binaire (blanc = 1, noir = 0, comme l'annexe)
def binariser(pixels_code,seuil):
    profil_binaire = (pixels_code > seuil).astype(int)
    indices_noires= np.where(profil_binaire == 0)[0]
    print('taille du profil= '+str(len(profil_binaire)))
    nb_barres = np.sum(np.diff(profil_binaire) == 1)
    print('nombre de barres= '+ str(nb_barres))
    return profil_binaire, indices_noires

def debut_fin_code(x1,y1,x2,y2,L,indices_noires):
    debut= indices_noires[0]
    fin= indices_noires[-1]
    xdebut= x1 + (debut/(L-1))*(x2-x1)
    ydebut= y1 + (debut/(L-1))*(y2-y1)
    xfin= x1 + (fin/(L-1))*(x2-x1)
    yfin= y1 + (fin/(L-1))*(y2-y1)
    return xdebut,ydebut,debut,xfin,yfin,fin

# MODIF : rayons=None pour ne plus cliquer à l'import du fichier
def lecture(img, rayons= None):
    
    if rayons is None:
        rayons= [rayon_manuel(img)]
    
    for rayon in rayons:
        
        x1,y1= rayon[0]
        x2,y2= rayon[1]
        
        
        L, pixels_code= signature(x1,y1,x2,y2,img)
        if L<95:
            print("le rayon de num°"+str(rayons.index(rayon)+1)+" n'est pas suffisant")
            continue
        N=256
        h, _ = np.histogram(pixels_code, bins=N, range=(0, 256))
        
        # Dénominateur commun : somme de 0 à N-1 de h(i) (vaut len(profil))
        total_pixels = np.sum(h)
        
        # 3. Calcul de w(k) et mu(k) pour k allant de 0 à 255
        i = np.arange(N)
        w = np.cumsum(h) / total_pixels
        mu = np.cumsum(i * h) / total_pixels
        # Moyenne totale mu(N - 1)
        mu_total = mu[-1]
        # 4. Calcul du critère crit(k)
        crit = w * (mu_total - mu)**2 + (1 - w) * (mu**2)
        seuil_otsu = np.argmax(crit)
        
        print('seuil de Otsu= '+ str(seuil_otsu))
        
        # MODIF : courbe du critère d'Otsu --------------------------------
        plt.figure(figsize=(8, 3))
        plt.plot(i, crit)
        plt.axvline(seuil_otsu, color='r', linestyle='--', label='seuil = '+str(seuil_otsu))
        plt.xlabel('k')
        plt.ylabel('crit(k)')
        plt.title("Critère d'Otsu")
        plt.grid(True)
        plt.legend()
        plt.show()
        #------------------------------------------------------------------

        
        # visualsation --------------------------------------------------------
        # rayonne= img.copy()
        # rayonne[np.round(yk).astype(int),np.round(xk).astype(int)]= img.max() +1        
        # plt.figure()
        # plt.imshow(rayonne, cmap= gray_red)
        # plt.title('visualisation des pixels traversés (en rouge)')
        # plt.plot()
        #----------------------------------------------------------------------
        
        
        profil_binaire, indices_noires= binariser(pixels_code,seuil_otsu)
        xdebut,ydebut,debut,xfin,yfin,fin= debut_fin_code(x1,y1,x2,y2,L,indices_noires)
        
        # MODIF : u entier calculé AVANT la 2ème extraction, puis L_finale = 95*u
        longueur_utile= np.sqrt((xfin-xdebut)**2+(yfin-ydebut)**2)
        u= max(1, int(round(longueur_utile/95)))
        L_finale= 95*u
        print('longueur du rayon utile= '+str(longueur_utile))
        print('u= '+ str(u))
        print('L_finale= '+str(L_finale))
        
        # MODIF : 2ème signature avec L imposé, binarisée avec le MÊME seuil
        L_finale, pixels_code_finales= signature(xdebut,ydebut,xfin,yfin,img,L_finale)
        profil_cb, indices_noires_2= binariser(pixels_code_finales,seuil_otsu)
        
        # vérification : le 1er et le dernier noir doivent être proches de 0 et L_finale-1
        print('1er noir= '+str(indices_noires_2[0])+' ; dernier noir= '+str(indices_noires_2[-1])+' ; L_finale-1= '+str(L_finale-1))
        
        plt.figure(figsize=(12, 3))
        plt.plot(profil_cb)
        plt.grid(True)
        plt.title('Signature binaire utile (blanc=1, noir=0)')
        plt.show()
    
    #on retourne le profil binaire et u pour le décodage
    return profil_cb, u

    

profil, u= lecture(img)

def decoder(profil,u,flag=0):
    #blanc = 1, noir = 0
    if flag==1:
        print("vous avez deja essayer les deux senses")
        return False
    profil_reduit= [np.round(sum(profil[i:i+u]) / u).astype(int) for i in range(0,len(profil),u)]
    for indice in range(len(profil_reduit)):
        if profil_reduit[indice]==1:
            profil_reduit[indice]='B'
        else:
            profil_reduit[indice]='N'
    code=["".join(profil_reduit[i:i+7]) for i in range(0, len(profil_reduit), 7)]
    code=["A" if x in A else "B" if x in B else "C" if x in C else x for x in code]
    if bool(set(code) - {"A", "B", "C"}):
        return decoder(profil[::-1],u,)
    return code
    
    print(code)
    # return code
    
#ne doit pas depasser un millieme de seconde
decoder(profil,u)

print(f"Finished in {time.time() - start_time:.2f} seconds")