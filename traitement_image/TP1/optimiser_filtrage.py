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

def filtrage_bilateral(img, v_size = 5, sigma_s = 3, sigma_c = 0.25):
    
    #prend en compte rgb ou niveaux de gris
    h,w =  img.shape[:2]

    #Paramètres de l'énoncé sont par défaut 
    img_bf = img*0
    #matrice spatiale
    M_sp = np.exp(-np.array([[np.sqrt(k**2 + l**2) for l in range(-v_size, v_size+1)] for k in range(-v_size, v_size+1)])/(2*sigma_s**2))
    # w_spatiale= np.sum(M_sp)
    
    #Parcours de tous les pixels de l’image
    for i in range(v_size,h-v_size):
        print(i)
        for j in range(v_size,w-v_size):
            # sum_w = 0
            # sum_wi= 0
            #Sélection d’une fenêtre (2*v_size+1)x(2*v_size+1) ajustée
            fenetre= img[i-v_size: i+v_size+1 , j-v_size: j+v_size+1]
            vecteur= fenetre - img[i, j]
            
            if len(img.shape) == 2:
                    norme = np.abs(vecteur)
            if len(img.shape) == 3:
                    norme = np.linalg.norm(vecteur, axis=2) #norme sur canaux rgb
                
            M_couleur= np.exp(-norme/ (2*sigma_c**2))
            poids= M_couleur * M_sp
            if len(img.shape) == 2: img_bf[i,j]= np.sum(poids*fenetre)/np.sum(poids)
            if len(img.shape) == 3: img_bf[i,j]= np.sum(poids[:,:, np.newaxis]*fenetre, axis= (0,1))/np.sum(poids)
    plt.figure()
    plt.subplot(1,2,1)
    plt.title('original')
    plt.imshow(img)
    plt.subplot(1,2,2)
    plt.imshow(img_bf)
    plt.title(f'filtred (v_size= {v_size}, sigma_s= {sigma_s}, sigma_c= {sigma_c})')


        #...

#filtrage_bilateral(img)
#filtrage_bilateral(barbara)
#filtrage_bilateral(temple)
filtrage_bilateral(face)
filtrage_bilateral(face, v_size= 10)
filtrage_bilateral(face, sigma_c= 2.5)
filtrage_bilateral(face, sigma_s= 30)
print(f"Finished in {time.time() - start_time:.2f} seconds")





#plot
# plt.figure()
# plt.imshow(img)
# plt.title('crop')
