import matplotlib.pyplot as plt
import numpy as np
#import numpy as np
#import skimage.color
#import skimage.transform

#from matplotlib import cm
#from matplotlib.colors import ListedColormap

img = plt.imread('./img/cameraman.tif')
bdx = plt.imread('./img/bdx.jpg')

plt.figure()
plt.subplot(2,1,1)
plt.imshow(img, cmap='jet')
plt.colorbar()

plt.subplot(2,1,2)
plt.imshow(bdx)
plt.colorbar()

Imax= np.max(img)
Imin= np.min(img)

## tracé d'histogramme:

#n = comptes, intensity = frontières des bins
n, intensity = np.histogram(img.ravel(), bins=256, range=(0, 256))

# On prend le début de chaque bin pour centrer les barres
bin_centers = intensity[:-1]

plt.figure()
# plt.bar crée de vraies barres pleines bien visibles
plt.bar(bin_centers, n, width=1.0, color='gray', edgecolor='none')

plt.xlabel('Intensités (0-255)')
plt.ylabel('Nombre de pixels')
plt.title("Histogramme")
plt.show()