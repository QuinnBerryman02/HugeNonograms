<h1>Huge Nonograms</h1>
This is a project where I 
am attempting to automatically generate large (1000x1000) 
nonograms/picross puzzles, as the normal sizes are typical less than 100x100. 
<br><br>
As images typically have thousands of colors with very slight variations, it is necessary to compress the color palatte down to a more resonable size, typically less than a dozen.
<br><br>
This project began with experimentation on ways to automatically compress the quantity of colors while still preserving the images quality to a viewer.
<br><br>
<image src="images/book.png">
The above image shows the diffence in quality between the original image, an moderately reduced palette size (~20) and a tiny palette size (5-6).
<br><br>
An initial mockup of the nonogram program was created showing the image alongside the hints on the horizontal and vertical edges.
<br><br>
Zooming and scrolling was also implemented as shown below allowing for detailed inspection of the image.
<br><br>
<image src="images/mountainExample1.png">
More work is needed to cut down on the amount of hints shown, as even with 30x30 windows on a bigger image, all hints are shown, leading to impossible to see hints as seen above.
