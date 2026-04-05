using UnityEngine;
using UnityEngine.Networking;
using TMPro;
using System.Collections;

public class SensorReader : MonoBehaviour
{
    public TMP_Text sensorDataText;

    private string apiUrl = "http://192.168.0.90:8080/hong_babo";

    IEnumerator Start()
    {
        while (true)
        {
            using (UnityWebRequest webRequest = UnityWebRequest.Get(apiUrl))
            {
                yield return webRequest.SendWebRequest();

                if (webRequest.result != UnityWebRequest.Result.Success)
                {
                    Debug.LogError("Error: " + webRequest.error);
                }
                else
                {
                    string jsonResponse = webRequest.downloadHandler.text;
                    Debug.Log(jsonResponse);
                    sensorDataText.text = jsonResponse;
                }
            }
            yield return new WaitForSeconds(2f);
        }
    }
}
