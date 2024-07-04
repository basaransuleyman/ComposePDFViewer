# Jetpack Compose PDF Display & Download & Share

The goal of this project is to handle a PDF file URL received from the backend by performing the following operations:

# 1. Retrieve Receipt Information: Obtain receipt information and PDF URL from the backend.
# 2. Download PDF: Download the PDF file using the provided URL.
# 3. Display PDF: Show the downloaded PDF to the user.
# 4. Share PDF: Allow the user to share the PDF file.


 PdfViewer(file: File)
* Purpose: Display the provided PDF file to the user.
* Operation: Reads the PDF file and uses PdfRenderer to render it as a bitmap. The user can zoom and pan the image with .pointerInput(Unit).
* Key Points: The PDF page is converted to a bitmap and displayed using a Composable UI component


downloadPdf(fileUrl: String)
* Purpose: Download the PDF file from the provided fileUrl and store it locally.
* Operation: Initiates the file download using DownloadPdfUseCase. Provides appropriate feedback upon completion or failure of the download.
* Key Points: Updates the UI state when the file is successfully downloaded and stores the PDF file in local storage.

sharePdf(context: Context, file:File)
* Purpose: Perform the necessary actions to share the PDF file.
* Operation: Initiates the sharing process using SharePdfUseCase and an appropriate intent.
* Key Points: Manages the necessary permissions and URI correctly during the sharing process.
