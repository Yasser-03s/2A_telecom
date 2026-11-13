import matplotlib.pyplot as plt
import numpy as np
import scipy.signal
import skimage.measure.label

img= plt.imread('./Images_Sujet2/coins_cross_Sujet2.png')

print(img.shape)
r= img[:,:, 0]
g= img[:,:, 1]
b= img[:,:, 2]


#image en niveaux de gris en moyennant sur les 3 canaux
nvgris= (r+g+b)/3

#changement d'image (utilisation de coins.png)
coins= plt.imread('./Images_Sujet2/coins.png')


#seuillage par alpha
alpha = 0.5
seuillage= coins > alpha

#amelioration
H= np.array([[0,1,0],[1,1,1],[0,1,0]])/5 #filtre du type 4 voisins

#dilatation
dilate= scipy.signal.convolve2d(seuillage,H, mode='same') > 0
#erosion
enhanced= scipy.signal.convolve2d(dilate,H, mode='same') == 1



#filtre de Sobel

fsobelX= np.array([[1,0,-1],[2,0,-2],[1,0,-1]])/8
fsobelY= fsobelX.T

beta= 0.025
filtredX= scipy.signal.convolve2d(enhanced,fsobelX, mode='same')
filtredY= scipy.signal.convolve2d(enhanced,fsobelY, mode='same')
sobel_filtred= np.sqrt(filtredX**2 + filtredY**2)>beta
#mieux:

# filtredX= scipy.signal.convolve2d(enhanced,fsobelX, mode='same')
# filtredY= scipy.signal.convolve2d(enhanced,fsobelY, mode='same')
# sobel_enhanced= np.sqrt(filtredX**2 + filtredY**2)>beta


#affichage
plt.figure()
plt.subplot(1,3,1)
plt.imshow(coins, cmap= 'gray')
plt.title('originale')
plt.subplot(1,3,2)
plt.imshow(seuillage,cmap= 'gray')
plt.title('niveaux de gris')
plt.subplot(1,3,3)
plt.imshow(enhanced,cmap= 'gray')
plt.title('amelioration')
plt.show()

plt.figure()
plt.subplot(1,2,1)
plt.imshow(coins, cmap= 'gray')
plt.title('originale')
plt.subplot(1,2,2)
plt.imshow(sobel_filtred, cmap= 'gray', vmax=1, vmin=0)
plt.title(f'filtre Sobel (seuil={beta})')


