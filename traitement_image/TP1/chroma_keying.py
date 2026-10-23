import matplotlib.pyplot as plt
import numpy as np
import skimage.color
import skimage.transform

from matplotlib import cm
from matplotlib.colors import ListedColormap


img= plt.imread("./img/pool.tif")

people= plt.imread("./img/people.jpg")
shape=np.shape(people)

metro = plt.imread("./img/metro.jpg")
background= plt.imread("./img/background.jpg")
foreground= plt.imread("./img/foreground.jpg") 

def composantes_ycbcr(img)  :
    ycbcr= skimage.color.rgb2ycbcr(img)
    h,w,channels = ycbcr.shape
    y= ycbcr[:,:, 0]
    cb= ycbcr[:,:, 1]
    cr= ycbcr[:,:, 2]
    

    #visualisation des composantes YCbCr
    
    plt.Figure()
    plt.subplot(2,3,1)
    plt.title("Composante Y")
    plt.imshow(y, cmap= 'gray')

    plt.subplot(2,3,2)
    plt.title("Composante Cb")
    plt.imshow(cb, cmap= 'gray')

    plt.subplot(2,3,3)
    plt.title("Composante Cr")
    plt.imshow(cr, cmap= 'gray')

    
    plt.show()
    
def composantes_rgb(img):
    r= img[:,:, 0]
    g= img[:,:, 1]
    b= img[:,:, 2]
    
    #visualisation des composantes RGB
    
    plt.Figure()
    plt.subplot(2,3,4)
    plt.title("Composante R")
    plt.imshow(r, cmap= 'gray')
    plt.subplot(2,3,5)
    plt.title("Composante G")
    plt.imshow(g, cmap= 'gray')
    plt.subplot(2,3,6)
    plt.title("Composante B")
    plt.imshow(b, cmap= 'gray')
    
    
def mask_cb(img,seuil):
    ycbcr= skimage.color.rgb2ycbcr(img)
    cb= ycbcr[:,:, 1]
    mask= cb<seuil
    return mask    

def mask_b(img,seuil):
    b= img[:,:, 1]
    mask= b<seuil
    return mask  


def masque_bleu(img,background,seuil):
    ycbcr_img= skimage.color.rgb2ycbcr(img)
    ycbcr_background= skimage.color.rgb2ycbcr(background)
    # m= mask(img,seuil)
    # ycbcr_img[:,:, 0]= ycbcr_img[:,:, 0]*m+ ycbcr_background[:,:, 0]*(1-m)
    # ycbcr_img[:,:, 1]= ycbcr_img[:,:, 1]*m+ ycbcr_background[:,:, 1]*(1-m)
    # ycbcr_img[:,:, 2]= ycbcr_img[:,:, 2]*m+ ycbcr_background[:,:, 2]*(1-m)
    #version optimisée:
    m= mask_cb(img,seuil)[:,:, np.newaxis]
    ycbcr_img= ycbcr_img*m + ycbcr_background*(1-m)
    result= skimage.color.ycbcr2rgb(ycbcr_img)
    return result
    
def compare_ycbcr_rgb(img,seuil):
    ycbcr= skimage.color.rgb2ycbcr(img)
    m1= mask_cb(img,seuil)[:,:, np.newaxis]
    result_ycbcr= skimage.color.ycbcr2rgb(ycbcr*m1)
    
    m2=mask_b(img,seuil)[:,:, np.newaxis]
    result_rgb= img*m2
    
    plt.figure()
    plt.subplot(1,2,1)
    plt.imshow(result_ycbcr)
    plt.title("avec YCbCr seuil %i " %170)
    plt.subplot(1,2,2)
    plt.imshow(result_rgb)
    plt.title("avec RGB seuil %i " %170)
    
# compare_ycbcr_rgb(people,170)

plt.figure()
plt.subplot(2,3,1)
plt.title("Masque bleu pour le seuil %i (sur canal Cb)" %180)
plt.imshow(masque_bleu(people,metro, 180))

plt.subplot(2,3,2)
plt.title("Application à background (reisized foreground)")
plt.imshow(masque_bleu(people,skimage.transform.resize(background,shape), 180))

plt.subplot(2,3,3)
plt.title("Application à foreground (reisized foreground)")
plt.imshow(masque_bleu(people,skimage.transform.resize(foreground,shape), 180))


plt.subplot(2,3,5)
plt.title("Application à background (reisized people)")
plt.imshow(masque_bleu(skimage.transform.resize(people,np.shape(background)),background, 180))

plt.subplot(2,3,6)
plt.title("Application à foreground (reisized people)")
plt.imshow(masque_bleu(skimage.transform.resize(people,np.shape(foreground)),foreground, 180))


#h,w,c= np.shape(people)
