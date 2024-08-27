using UnityEngine;
using UnityEngine.Networking;
using System.Collections;

public class webpage : MonoBehaviour
{
    public int imageWidth = 1920;
    public int imageHeight = 1080;
    private Texture2D imageTexture;
    private bool isUploading = false;

    void Start()
    {
        imageTexture = new Texture2D(imageWidth, imageHeight, TextureFormat.RGB24, false);
        StartCoroutine(CaptureAndUpload());
    }

    IEnumerator CaptureAndUpload()
    {
        while (true)
        {
            yield return new WaitForSeconds(5);

            if (!isUploading)
            {
                StartCoroutine(UploadImage());
            }
        }
    }

    IEnumerator UploadImage()
    {
        isUploading = true;

        yield return new WaitForEndOfFrame();
        imageTexture.ReadPixels(new Rect(0, 0, imageWidth, imageHeight), 0, 0);
        imageTexture.Apply();

        byte[] imageData = imageTexture.EncodeToJPG();

        string url = "http://192.168.137.57:3000/create";

        WWWForm form = new WWWForm();
        form.AddBinaryData("picture", imageData, "image.jpg", "image/jpeg");

        UnityWebRequest www = UnityWebRequest.Post(url, form);

        yield return www.SendWebRequest();

        if (www.result != UnityWebRequest.Result.Success)
        {
            Debug.LogError("Image upload failed: " + www.error);
        }
        else
        {
            Debug.Log("Image uploaded successfully!");
        }

        isUploading = false;
    }
}
