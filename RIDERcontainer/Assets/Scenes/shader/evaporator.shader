Shader "Custom/evaporator"


{
    Properties
    {
        _MainTex ("Albedo (RGB)", 2D) = "gray" {}
        _BumpMap ("NormalMap", 2D) = "bump" {}
        _RimColor ("RimColor", Color) = (1,1,1,1)
        _RimPower ("RimPower", Range(1, 10)) = 1
        _BlinkSpeed ("Blink Speed", Float) = 5
    }
    SubShader
    {
        Tags { "RenderType"="Opaque" }
        LOD 200

        CGPROGRAM
        
        #pragma surface surf Standard fullforwardshadows

        sampler2D _MainTex;
        sampler2D _BumpMap;
        fixed4 _RimColor;
        float _RimPower;
        float _BlinkSpeed;

        struct Input
        {
            float2 uv_MainTex;
            float2 uv_BumpMap;
            float3 viewDir;
        };

        void surf (Input IN, inout SurfaceOutputStandard o)
        {
            // Albedo comes from a texture tinted by color
            fixed4 c = tex2D(_MainTex, IN.uv_MainTex);
            o.Albedo = c.rgb;
            o.Normal = UnpackNormal(tex2D(_BumpMap, IN.uv_BumpMap));
            o.Metallic = 0.0;
            o.Smoothness = 0.5;
            o.Alpha = c.a;

            // Rim effect
            float rim = saturate(dot(normalize(IN.viewDir), o.Normal));
            float rimEffect = pow(1 - rim, _RimPower);

            // Calculate the blink effect using time
            float blink = _BlinkSpeed > 0 ? sin(_Time.y * _BlinkSpeed) * 0.5 + 0.5 : 0;

            // Combine rim and blink effects
            o.Emission = rimEffect * _RimColor.rgb * blink;
        }
        ENDCG
    }
    FallBack "Diffuse"
}
