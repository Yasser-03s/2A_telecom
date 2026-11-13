import matplotlib.pyplot as plt
import numpy as np
import scipy.signal
from random import randrange

img= plt.imread("./EAN13.jpg");
bw= (img[:,:, 0]+img[:,:, 1]+img[:,:, 2])/3;

P = range(-10,10+1)
X, Y = np.meshgrid(P,P)

sig = 1
Gx = -X/(2*np.pi*sig**4)*np.exp(-(X**2+Y**2)/(2*sig**2))
Gy = -Y/(2*np.pi*sig**4)*np.exp(-(X**2+Y**2)/(2*sig**2))


img_x= scipy.signal.convolve2d(bw, Gx, mode='same')
img_y= scipy.signal.convolve2d(bw, Gy, mode='same')


norme= np.sqrt(img_x ** 2 + img_y**2)
#normaliser entre 0 et 1
norme/= np.max(norme)

plt.subplot(1,2,1)
plt.imshow(img_x)
plt.title('d/dx (filtreCanny)');
plt.subplot(1,2,2)
plt.imshow(img_y)
plt.title('d/dy (filtreCanny)');




def non_supervised(img)    :
    h,w =  img.shape[:2];
    randrange( )
    randrange( )
    hist

#idée est de generer des segments aleatoire jusque avoir un qui traverse le code diagonalement cad rentre en pixel noir et sort du pixel noir