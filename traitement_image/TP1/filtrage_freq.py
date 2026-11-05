import matplotlib.pyplot as plt
import numpy as np
import scipy.signal
import scipy.ndimage

img = plt.imread('./img/pise_ext.bmp')

def spectre(data):
    #log module de transformee de fourier centree en 0, +1 pour ne pas avoir d'erreur du type log(0)
    return np.log10(1+np.abs(np.fft.fftshift(np.fft.fft2(data))))


plt.figure()
plt.subplot(2,2,1)
plt.imshow(img, cmap= 'gray')
plt.title('originale')

plt.subplot(2,2,3)
plt.imshow(spectre(img), extent=[-0.5, 0.5, 0.5, -0.5])
#fixer les axes pour interpreter les frequences
plt.ylabel('Fy')
plt.xlabel('Fx')


filtre_carre= np.ones((5,5))/(5*5)

img_filtred= scipy.signal.convolve2d(img,filtre_carre,mode='same')

plt.subplot(2,2,2)
plt.imshow(img_filtred, cmap= 'gray')
plt.title('moyenneur uniforme')

plt.subplot(2,2,4)
plt.imshow(spectre(img_filtred), extent=[-0.5, 0.5, 0.5, -0.5])
plt.title('spectre de img filtrée')
#fixer les axes pour interpreter les frequences
plt.ylabel('Fy')
plt.xlabel('Fx')

#Le bruit est concentré à une fréquence précise, pas sur toute la bande haute.
# Un passe-bas détruit donc plus d’information utile que de bruit.
# moyenneur plus grand => le bruit diminue mais l’image devient très floue

# Solution: construction d'un filtre passe - bande\

    
## étape 1:

#on prend size pres de 3 sigma pour avoir une valeur au bord 1% du max
#G.sum() pour verifier choix de sigma
size= 6
X, Y = np.meshgrid(range(-size,size+1), range(-size,size+1))

#filtre gaussien

sigma=2

#pour centrer la gaussienne: range(-n, n+1) (pythong range +1)
G= 1/(2*np.pi*sigma*sigma) *np.exp(-((X**2+Y**2)/(2*sigma**2)))

## étape 2:

#le curseur donne 
fx= 0.2524
fy= -0.2477

G_pbande = G * 2*np.cos(2*np.pi*(fx*X+fy*Y))

dirac = np.zeros((13, 13))
dirac[13 // 2, 13 // 2] = 1.0 

G_cbande = dirac - G_pbande

plt.figure()
plt.subplot(2,1,1)
plt.imshow(G_cbande)
plt.title('filtre gaussien cbande')
plt.subplot(2,1,2)
plt.imshow(spectre(G_cbande))
plt.ylabel('Fy')
plt.xlabel('Fx')


final= scipy.signal.convolve2d(img,G_cbande,mode='same')

plt.figure()
plt.imshow(final, cmap= 'gray')
plt.title('resultat final')