# Frequently Asked Questions

### Why does Anonimatron use pseudorandom generators?
This is a speed/safetly tradeoff. Anonimatron is typically used with large datasets, where processing the dataset takes
time. A SecureRandom generator would add a performance hit, while adding only little to no extra security. The reason for
this is that the random generators are not used for cryptographic functions. Most anonymizers generate a new, unrelated
piece of data which is typically not (entirely) based on the input.