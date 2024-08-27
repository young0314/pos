using UnityEngine;

public class RotateObject : MonoBehaviour
{
    // 회전 속도를 조절할 변수
    public float rotationSpeed = 250f;
    // 사용자가 지정한 회전 축
    public Vector3 rotationAxis = new Vector3(0f, 0f, 1f); // 기본적으로 z 축 주위로 회전하도록 설정됩니다.

    void Update()
    {
        // 회전 축을 기준으로 GameObject를 회전시키는 코드
        transform.Rotate(rotationAxis, rotationSpeed * Time.deltaTime);
    }
}
