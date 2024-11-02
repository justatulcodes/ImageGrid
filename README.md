# ImageGrid

This is the solution to the assignment problem assigned to me that asked me to develop an application to efficiently load and display images in a scrollable grid without using any third party library.

The application is build using Jetpack compose. 

I chose Jetpack Compose because the existing Acharya Prashant android app was also built using that. 

# App Screenshot

  ### Home Page
  - <img src="https://github.com/user-attachments/assets/6aebb2f0-1026-407c-a396-42f5b1a11bd6" width = "300" height="670">
  ### Details Page
  - <img src="https://github.com/user-attachments/assets/47161c63-c806-4f56-9af0-4fd2b80722ac" width = "300" height="670">
  - <img src="https://github.com/user-attachments/assets/8517d13d-ed1a-40b2-b432-e220024d4039" width = "300" height="670">

# Running for the first time

The application does not require any preliminary setup to run. Just clone and run the project.

# Demo and APK

## Demo Video

The demo video with commentary is uploaded on drive and can be watched here: https://drive.google.com/file/d/1i0S5sUG6iQsArFIK8-DmX3nk88ZfDWic/view?usp=drive_link

## Code Overview

There is also a code structure overview video if its needed for quick reference: https://drive.google.com/file/d/1-c3jSnc2OQTDYL-r4YBw9mPNPPx_r224/view?usp=drive_link

## Release Build

Release Build of the project could be downloaded from Release section of this repository or from this link: https://drive.google.com/file/d/1WVp18H9cVUKxUQBXyFqBSqypJUgax0hh/view?usp=sharing

# How is caching done?

I am using LRU algorithm for Disk cache and Memory cache. for grid view, I am caching a smaller 150x150 sized version. For details page, I am storing the compressed version of full sized image.

The flow is like this: 
- We send request to download the image
- Before I download, I check if the image is available in memory cache
    - If it is available, I return that image
    - If not, then I check my disk cache
        - If image is available in disk cache, I save that in memory cache and return
        - If not available, I download image
- Once image is downloaded, I cache that image in both disk and memory and return it.


# Some Other Tech Choices

### Why I didn't use retrofit?

Since I was not supposed to use any Image Caching library and there was only 1 API endpoint, I thought to just use native java library for fetching the API response.

### Why didn't I call API again for every 10 images?

While my initial plan was to call the API for fetching every 10 images so I don't put load on server by calling 100 images at once, the plan failed because I did not have any information about API.

Since every response starts from same image always (i.e. call for 10 images and call for 20 images will both contain first same 10 images), It was better to just call all 100 items at once and use them across pages.

### Why compress image 150x150 for grid view?

Each image I fetched was in MBs which not only takes more bandwidt and battery but also is not possible to cache. Anyhow, I had to compress the image before I could cache it. Grid view is more like a thumbnail view where high quality does not add value.

Also, in the Acharaya Prashant app, similar can be seen on the Shorts page as all thumbnail there are compressed to 150x150 if my estimation is correct. Hence I did the same.
