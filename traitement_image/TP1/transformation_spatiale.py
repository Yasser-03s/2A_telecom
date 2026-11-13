import matplotlib.pyplot as plt
import numpy as np
import scipy.signal
import scipy.ndimage
from scipy.interpolate import RectBivariateSpline
import time; start_time = time.time()


img= plt.imread('./img/cameraman.tif')
h,w= img.shape[:2]



#pavage vertical et horizontal
x= range(-w//2, w//2)
y= range(-h//2, h//2)
X,Y = np.meshgrid(x,y)

#angle de rotation
theta= np.deg2rad(45)
R= np.array([[np.cos(theta), - np.sin(theta)],[np.sin(theta), np.cos(theta)]])

#application de la rotation
rotated_X= np.cos(theta)*X - np.sin(theta)*Y
rotated_Y= np.sin(theta)*X + np.cos(theta)*Y

spl= RectBivariateSpline(y,x,img, kx=2, ky= 2)
pp= spl.ev(rotated_Y, rotated_X)

plt.figure()
plt.imshow(pp, cmap= 'gray')