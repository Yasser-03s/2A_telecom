import matplotlib.pyplot as plt

from matplotlib import cm
from matplotlib.colors import ListedColormap


#plt.ginput(2) function, which pauses execution until you click twice on the image and returns the (x, y)

import numpy as np
import scipy.signal
from random import randrange
import time; start_time = time.time()

#------------ création d'une palette gray red pour verifier que l'échantillonage des pixels est bon-------------

gray_red= ListedColormap(np.vstack([cm.gray(np.linspace(0, 1, 256)), [1, 0, 0, 1]]))

#---------------------------------------------------------------------------------------------------------------




img= plt.imread("./EAN13.jpg");
bw= (img[:,:, 0]+img[:,:, 1]+img[:,:, 2])/3;


# img = mpimg.bw


def rayon_manuel(img):
    plt.imshow(img)
    print("Click 2 points on the image...")
    points = plt.ginput(2)

    plt.close() 
    return points

def lecture(img, rayons= [rayon_manuel(img)]):
    
    for rayon in rayons:
        
        x1,y1= rayon[0]
        x2,y2= rayon[1]
        
        
        L= max(95, int(np.ceil(np.sqrt((x2-x1)**2+(y2-y1)**2))))
        
        t= np.linspace(0,1,L)
        xk= x1 + t * (x2 - x1)
        yk= y1 + t * (y2 - y1)
    
        #methode plus proche voisin: pixel=[ligne(y), colonne(x)]
        pixels_qr=img[np.round(yk).astype(int),np.round(xk).astype(int)]     #  l' extraction des pixels coupés par le rayon 
        N=256
        h, _ = np.histogram(pixels_qr, bins=N, range=(0, 256))
        
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
        profil_binaire = (pixels_qr > seuil_otsu).astype(int)
        
        plt.figure(figsize=(12, 3))
        plt.plot(profil_binaire)
        plt.grid(True)
        plt.show()
        
        print('seuil de Otsu= '+ str(seuil_otsu))
        print('taille du profil= '+str(len(profil_binaire)))
        nb_barres = np.sum(np.diff(profil_binaire) == 1) + profil_binaire[0]
        print('nombre de barres= '+ str(nb_barres))
        
        # visualsation --------------------
        # rayonne= img.copy()
        # rayonne[np.round(yk).astype(int),np.round(xk).astype(int)]= img.max() +1        
        # plt.figure()
        # plt.imshow(rayonne, cmap= gray_red)
        # plt.title('visualisation des pixels traversés (en rouge)')
        # plt.plot()
        #----------------------------------
        
        
        indices_noires= np.where(profil_binaire == 1)[0]
        debut= indices_noires[0]
        fin= indices_noires[-1]
        largeur= fin -debut +1
        u= int(np.round(largeur/95))
        print('largeur du code= '+str(largeur))
        print('u= '+ str(u))
        
    
    return profil_binaire   
    

    

profil= lecture(bw)

print(f"Finished in {time.time() - start_time:.2f} seconds")