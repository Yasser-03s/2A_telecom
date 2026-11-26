import numpy as np
import matplotlib.pyplot as plt
from scipy.io import loadmat


# ============================================================
# Loading the Urban hyperspectral dataset
# ============================================================

# Hyperspectral image
data = loadmat("Urban_R162.mat")
max_val = float(np.asarray(data["maxValue"]).squeeze())

# Matrix of labels
Y = np.asarray(data["Y"], dtype=float)/max_val
print(Y.shape)

# Dimensions
n,_ = Y.shape
nb_row = int(np.asarray(data["nRow"]).squeeze())
nb_col = int(np.asarray(data["nCol"]).squeeze())
print("Image size = %s x %s" % (nb_row,nb_col))

# Endmembers matrix
ground_truth = loadmat("end4_groundTruth.mat")
X = np.asarray(ground_truth["M"], dtype=float)
_,n = X.shape 
endmember_names = ["Asphalt", "Grass", "Tree", "Roof"]
print("Number of endmembers n=",n)

# Filtered wavelengths (nm)
wavelengths = np.arange(410, 2501, 10)
removed = (
    list(range(1, 5))
    + [76, 87]
    + list(range(101, 112))
    + list(range(136, 154))
    + list(range(198, 211))
)
wavelengths = np.delete(wavelengths, np.array(removed) - 1)
p = len(wavelengths)
print("Number of bands p=", p)

# ============================================================
# Image display
# ============================================================

# Selected RGB wavelengths for display
R = np.argmin(abs(wavelengths - 650))
G = np.argmin(abs(wavelengths - 550))
B = np.argmin(abs(wavelengths - 450))

# RGB tensor
image = Y.reshape(p, nb_row, nb_col, order="F")
image = image.transpose(1, 2, 0)
rgb = np.stack([image[:, :, R], image[:, :, G], image[:, :, B]],axis=2)
rgb = (rgb - rgb.min(axis=(0, 1))) / (rgb.max(axis=(0, 1)) - rgb.min(axis=(0,)))

plt.figure(figsize=(7, 7))
plt.imshow(rgb)
plt.axis("off")
plt.title("Image hyperspectrale Urban")
plt.show()
