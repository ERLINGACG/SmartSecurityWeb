package com.erling.service.device;

import com.erling.dao.device.DeviceMapper;
import com.erling.entity.device.Device;
import com.erling.service.exception.exc.DeviceBusinessException;
import com.erling.service.obj.ServiceObject;
import com.erling.utils.result.Result;
import com.erling.utils.result.ResultEnum;
import com.erling.utils.result.ren.DeviceResultEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DeviceService extends ServiceObject {



      DeviceMapper  deviceMapper;
      public DeviceService(DeviceMapper deviceMapper) {
          this.deviceMapper = deviceMapper;
      }

      @Transactional(rollbackFor = Exception.class)
      public ResponseEntity<Result<?>> addDevice(Device device, BindingResult result)  {
              validate(result);
              device.setDate(LocalDateTime.now());
              return ResponseEntity.
                      ok(new Result<>(
                                      DeviceResultEnum.ADD_DEVICE_SUCCESS,
                                      deviceMapper.insertDevice(device)
                              )
                      );
      }

    public ResponseEntity<Result<?>> getDevicesByEmail(String email) {
        List<Device> devices = deviceMapper.getDevicesByEmail(email);
          return ResponseEntity.
                      ok(new Result<>(
                                 DeviceResultEnum.SELECT_DEVICE_LIST_SUCCESS,
                                 devices
                      )
          );

      }

      @Transactional(rollbackFor = Exception.class)
      public ResponseEntity<Result<?>> deleteDevice(int pid,String email) {
          boolean deleteCount = deviceMapper.deleteDevice(pid, email);
          if(deleteCount){
              return ResponseEntity.
                      ok(new Result<>(
                              DeviceResultEnum.DELETE_DEVICE_SUCCESS,
                              true
                      )
              );
          }else{
              throw new DeviceBusinessException(
                      DeviceResultEnum.DEVICE_NOT_FOUND
              );
          }
      }

      public ResponseEntity<Result<?>> getDevice(int pid,String email) {
          Device device = deviceMapper.getDeviceByPidAndEmail(pid,email);
          if(device != null){
              return ResponseEntity.
                      ok(new Result<>(
                                      DeviceResultEnum.GET_DEVICE_SUCCESS,
                                      device
                              )
                      );
          }else{
              throw new DeviceBusinessException(
                      DeviceResultEnum.DEVICE_NOT_FOUND
              );
          }

      }
    @Transactional(rollbackFor = Exception.class)
      public ResponseEntity<Result<?>> updateDevice(Device device, BindingResult result) {
          validate(result);
          Device oldDevice = deviceMapper.getDeviceByPidAndEmail(device.getPid(),device.getUserEmail());

          if( oldDevice!= null){
              if (!oldDevice.getDeviceTopic().equals(device.getDeviceTopic())) {
                  Device newDevice = deviceMapper.getDeviceByTopic(device.getDeviceTopic());
                  if (newDevice != null && !Objects.equals(newDevice.getPid(), device.getPid())) {
                      throw new DeviceBusinessException(
                              DeviceResultEnum.UPLOAD_DEVICE_EXISTS
                      );
                  }
              }
              device.setDate(LocalDateTime.now());
              return ResponseEntity.ok(new Result<>(
                      ResultEnum.DEVICE_UPDATE_SUCCESS,
                      deviceMapper.updateDevice(device)
              ));
          }
          throw new DeviceBusinessException(
                  DeviceResultEnum.DEVICE_NOT_FOUND
          );
      }

      public void test(){
          throw new DeviceBusinessException(
                  DeviceResultEnum.DEVICE_NOT_FOUND
          );
      }

}
