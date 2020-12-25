'use strict'

/**
 * Copyright(C),2020,haima.me
 * Author: lichuanrong@haima.me
 * Date: 2020.12.25
 * Version: 1.0.0
 * Description: core utils
 * Function List: 1.devices , 2.
 * History:
 * Others:
 **/

// -- dev id --
let devices = {};
devices.type = 'WEB';
devices.getId = () => {
    return new Promise((resolve,reject)=>{
        try{
            const startTime = new Date().getTime();
            let excludes = {};
            excludes.userAgent = true;
            excludes.audio = true;
            excludes.enumerateDevices = true;
            excludes.fonts = true;
            excludes.fontsFlash = true;
            excludes.webgl = true;
            excludes.canvas = true;
            let options = {excludes: excludes}
            Fingerprint2.get(options, function (components) {
                const values = components.map(function (component) {
                    return component.value
                });
                //指纹
                const murmur = Fingerprint2.x64hash128(values.join(''), 31);
                console.log('指纹',murmur);
                console.log('消耗(毫秒)',(new Date().getTime() - startTime));
                console.log('使用参数:')
                for (const c of components) {
                    console.log(c.key,c.value);
                }
                console.log('--- 参数 end ---')
                resolve(murmur);
            });

        } catch (e){
            reject(e);
        }
    })
};
// 获取ID
devices.getId().then((devId)=>{
    devices.setId(devId);
}).catch((err)=>{
    console.log('error',err);
});
//设置ID
devices.setId = (id) => {
    devices.devId = id;
};




