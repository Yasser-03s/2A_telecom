import matplotlib.pyplot as plt
import numpy as np
import skimage.color
import skimage.transform
import skimage.measure
from matplotlib.colors import ListedColormap

from matplotlib import cm
from matplotlib.colors import ListedColormap

mocap= plt.imread('./img/mocap.jpg')

def segment(img):
    ycbcr= skimage.color.rgb2ycbcr(img)
    mask= ycbcr[:,:, 0]>230
    m= mask[:,:, np.newaxis]
    L = skimage.measure.label(mask)
    
    # ycbcr[:,:, 0]*=mask
    # result= skimage.color.ycbcr2rgb(ycbcr)
    # plt.figure()
    # plt.imshow(result)
    
    map_= np.random.rand(np.max(L)+1, 3)
    map_1 = np.ones((np.max(L)+1,1))
    palette = np.concatenate((map_, map_1), axis=1)
    
    
    L_rgb = (palette[L, 0:3]*255).astype('uint8')
    result= mocap*(1-m) + L_rgb*m
    
    plt.figure()
    plt.imshow(result)
    plt.title("Resultat etiquettage")
    
plt.figure()
plt.imshow(mocap)
plt.title('Originale')
segment(mocap)