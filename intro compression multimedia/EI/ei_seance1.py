from PIL import Image
import numpy as np
import matplotlib.pyplot as plt

A=Image.open('./pool.tif')
print(A)
print(A.size)
# A.show()


# np.fromfile(input_file, dtype=np.uint8, count=Taille, offset)




h= 288
w= 352
size= h*w
hlow= h//2
wlow= w//2
sizelow= hlow*wlow

Y=np.fromfile('./news30f.yuv', dtype=np.uint8, count= size, offset= 0)
U=np.fromfile('./news30f.yuv', dtype=np.uint8, count= sizelow, offset= size)
V=np.fromfile('./news30f.yuv', dtype=np.uint8, count= sizelow, offset= size+sizelow)

Y= Y.reshape(h,w)
U= U.reshape(hlow,wlow)
V= V.reshape(hlow,wlow)

U_res= np.zeros((h,w),dtype=np.uint8)
U_res[::2, ::2]= U
U_res[1::2, ::2]= U
U_res[::2, 1::2]= U
U_res[1::2, 1::2]= U


V_res= np.zeros((h,w),dtype=np.uint8)
V_res[::2, ::2]= V
V_res[1::2, ::2]= V
V_res[::2, 1::2]= V
V_res[1::2, 1::2]= V


## --------------------- CONVERTIR EN FLOAT --------------------- 
Y=Y.astype('float')
U_res= U_res.astype('float')
V_res= V_res.astype('float')

R= np.maximum(np.minimum(1.164*(Y-16)+ 1.596*(V_res-128),255),0)
G= np.maximum(np.minimum(1.164*(Y-16)- 0.813*(V_res-128)- 0.391*(U_res-128),255),0)
B= np.maximum(np.minimum(1.164*(Y-16)+ 2.018*(U_res-128),255),0)
#faire attention a garder les valeurs entre 0 et 255
#on peut faire np.clip ou np.minimum ou logique booléenne avec des multiplications R*(R>=0)*(R<256)+255.0*(R>=256))

img= np.stack((R,G,B), axis=2).astype('uint8')
# plt.figure()
# img.show()
Image.fromarray(img,mode="RGB").show()