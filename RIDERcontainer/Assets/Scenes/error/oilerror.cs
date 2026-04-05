using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class oilerror : MonoBehaviour
{
    public RawImage rawImage;
    public string imageUrl = "http://192.168.137.156:8080/guipost";
    public string errorDataUrl = "http://192.168.137.156:8080/send_error";
    public float refreshInterval = 5f;
    public Renderer objectRenderer; // 물체의 Renderer를 참조하는 변수를 추가

 IEnumerator Start()
{
    objectRenderer = GetComponent<Renderer>();
    if (objectRenderer.material.HasProperty("_BlinkSpeed"))
    {
        objectRenderer.material.SetFloat("_BlinkSpeed", 0f); //  BlinkSpeed를 0으로 설정
    }

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

                if (objectRenderer.material.HasProperty("_BlinkSpeed"))
                {
                    if (errorData.Contains("6") || errorData.Contains("7") )
                    {
                        objectRenderer.material.SetFloat("_BlinkSpeed", 5f); // _BlinkSpeed를 5로 설정
                    }
                    else 
                    {
                        objectRenderer.material.SetFloat("_BlinkSpeed", 0f); // 해당 데이터가 없을 경우 _BlinkSpeed를 0으로 설정
                    }
                }
                else
                {
                    Debug.LogError("The shader does not have a '_BlinkSpeed' property.");
                }
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
