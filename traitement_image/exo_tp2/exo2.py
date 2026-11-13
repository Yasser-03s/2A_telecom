import matplotlib.pyplot as plt
import scipy.signal
import numpy as np

barbara= plt.imread('./Images_Sujet2/barbara.png')
lezard= plt.imread('./Images_Sujet2/lezard.png')
circles_noise= plt.imread('./Images_Sujet2/circles_sp_noise.png')

r= barbara[:,:, 0]
g= barbara[:,:, 0]*0
b= barbara[:,:, 0]*0

I= np.stack((r,g,b), axis=2)
plt.figure()
plt.imshow(I)
plt.title('barbara')

plt.figure()
plt.imshow(lezard, cmap='jet')
plt.title('lezard')




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


filtrage_bilateral(circles_noise)