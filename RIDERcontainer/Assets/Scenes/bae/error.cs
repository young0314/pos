using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class error : MonoBehaviour
{
    public RawImage rawImage;
    public string imageUrl = "http://192.168.137.156:8080/guipost";
    public string errorDataUrl = "http://192.168.137.156:8080/send_error";
    public float refreshInterval = 5f;

    IEnumerator Start()
    {
        while (true)
        {
            // 이미지 로딩
            using (WWW www = new WWW(imageUrl))
            {
                yield return www;

                if (string.IsNullOrEmpty(www.error))
                {
                    Texture2D texture = www.texture;
                    rawImage.texture = texture;
                }
                else
                {
                    Debug.LogError("Failed to load image: " + www.error);
                }
            }

            // 에러 데이터 받아오기
            using (WWW errorDataWWW = new WWW(errorDataUrl))
            {
                yield return errorDataWWW;

                if (string.IsNullOrEmpty(errorDataWWW.error))
                {
                    string errorData = errorDataWWW.text;
                    Debug.Log("Received error data from server: " + errorData);
                }
                else
                {
                    Debug.LogError("Failed to load error data: " + errorDataWWW.error);
                }
            }

            yield return new WaitForSeconds(refreshInterval);
        }
    }
}