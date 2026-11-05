import matplotlib.pyplot as plt
import numpy as np
import scipy.signal
import scipy.ndimage
import time; start_time = time.time()


barbara= plt.imread('./img/barbara_awgn_noise.png').astype('double')
temple= plt.imread('./img/temple.png')
face= plt.imread('./img/face.png')

# crop:
img= barbara[250:350,50:150]

def filtrage_bilateral(img):
    h, w,c= img.shape
    #Paramètres
    v_size = 5
    sigma_s = 3
    sigma_c = 0.25
    img_bf = img*0
    #matrice spatiale
    M_sp = np.exp(-np.array([[k**2 + l**2 for l in range(-v_size, v_size+1)] for k in range(-v_size, v_size+1)])/(sigma_s**2))
    w_spatiale= np.sum(M_sp)
    
    #Parcours de tous les pixels de l’image
    for i in range(v_size,h-v_size):
        print(i)
        for j in range(v_size,w-v_size):
            sum_w = 0
            sum_wi= 0
            #Sélection d’une fenêtre (2*v_size+1)x(2*v_size+1) ajustée
            fenetre= img[i-v_size: i+v_size+1 , j-v_size: j+v_size+1]
            vecteur= img[i, j] - fenetre
            M_couleur= np.exp(-np.linalg.norm(vecteur)/sigma_c**2)
            poids= M_couleur * M_sp
            img_bf[i,j]= np.sum(poids[:,:, np.newaxis]*fenetre, axis= (0,1))/np.sum(poids)
    plt.figure()
    plt.subplot(1,2,1)
    plt.title('original')
    plt.imshow(img)
    plt.subplot(1,2,2)
    plt.imshow(img_bf)
    plt.title('filtred')


        #...

filtrage_bilateral(face)
#filtrage_bilateral(barbara)
# filtrage_bilateral(temple)
print(f"Finished in {time.time() - start_time:.2f} seconds")





#plot
# plt.figure()
# plt.imshow(img)
# plt.title('crop')
