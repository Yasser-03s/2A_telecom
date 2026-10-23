import matplotlib.pyplot as plt
import numpy as np
import scipy.signal
import scipy.ndimage
import time; start_time = time.time()


barbara= plt.imread('./img/barbara_awgn_noise.png').astype('double')
face= plt.imread('./img/face.png')

# crop:
img= barbara[250:350,50:150]

h, w = img.shape
#Paramètres
v_size = 5
sigma_s = 3
sigma_c = 0.25
img_bf = img*0
#Parcours de tous les pixels de l’image
for i in range(v_size,h-v_size):
    print(i)
    for j in range(v_size,w-v_size):
        sum_w = 0
        sum_wi= 0
        #Sélection d’une fenêtre (2*v_size+1)x(2*v_size+1) ajustée
        for k in range(-v_size,v_size+1):
            
            for l in range(-v_size,v_size+1):
                vecteur= img[i,j] - img[i+k,j+l]
                w_couleur= np.exp(-np.linalg.norm(vecteur)/(sigma_c**2))
                w_spatiale= np.exp(-np.linalg.norm([k, l])/(sigma_s**2))
                
                sum_w+= w_couleur*w_spatiale
                sum_wi+= w_couleur*w_spatiale*img[i+k,j+l]
        img_bf[i,j]= sum_wi / sum_w

plt.figure()
plt.subplot(1,2,1)
plt.title('crop')
plt.imshow(img)
plt.subplot(1,2,2)
plt.imshow(img_bf)
plt.title('filtred crop')
        #...


print(f"Finished in {time.time() - start_time:.2f} seconds")





#plot
# plt.figure()
# plt.imshow(img)
# plt.title('crop')
